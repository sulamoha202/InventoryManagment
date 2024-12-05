package com.mtsd.repository.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mtsd.model.Product;
import com.mtsd.model.ReportLowStock;
import com.mtsd.model.ReportStockSummary;
import com.mtsd.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    private final SQLiteDatabase database;
    private final static String TABLE_NAME = "products" ;


    public ProductRepositoryImpl(SQLiteDatabase database){
        this.database = database;
    }
    @Override
    public boolean insert(Product product) {
        ContentValues values = new ContentValues();
        values.put("name",product.getName());
        values.put("quantity",product.getQuantity());
        values.put("price",product.getPrice());
        values.put("description",product.getDescription());
        values.put("imageReference",product.getImageReference());
        long result = database.insert(TABLE_NAME,null,values);
        return result != -1;
    }

    @Override
    public boolean updateProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("quantity", product.getQuantity());
        values.put("price", product.getPrice());
        values.put("description", product.getDescription());
        values.put("imageReference", product.getImageReference());
        int rowsAffected = database.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(product.getId())});
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteProductById(int productId) {
        int rowsAffected = database.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(productId)});
        return rowsAffected > 0;
    }

    @Override
    public Product getProductById(int productId) {
        Cursor cursor = database.query(TABLE_NAME, null, "id = ?", new String[]{String.valueOf(productId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
           Product product = getProduct(cursor);
            cursor.close();
            return product;
        }
        return null;
    }

    @Override
    public Product getProductByName(String productName) {
        Cursor cursor = database.query(TABLE_NAME, null, "name = ?", new String[]{String.valueOf(productName)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Product product = getProduct(cursor);
            cursor.close();
            return product;
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                products.add(getProduct(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    @Override
    public  int getQuantitySumOfAllProducts(){
        String query = "SELECT SUM(quantity) FROM "+TABLE_NAME;
        Cursor cursor = database.rawQuery(query,null);

        int totalQuantity = 0;
        if(cursor.moveToFirst()){
            totalQuantity = cursor.getInt(0);
        }
        cursor.close();
        return totalQuantity;
    }

    @Override
    public  int getLowStockCount(int threshold){
        String query = "SELECT SUM(quantity) FROM " + TABLE_NAME + " WHERE quantity <= ?";
        Cursor cursor = database.rawQuery(query,new String[]{String.valueOf(threshold)});

        int count = 0;
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    @Override
    public double getTotalInventoryValue(){
        double totalValue =0;
        Cursor cursor = database.rawQuery("SELECT SUM(price * quantity) AS total FROM products",null);

        if(cursor.moveToFirst()){
            totalValue = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return totalValue;
    }

    @Override
    public int getProductQuantityById(int productId) {
        Cursor cursor = database.rawQuery("SELECT quantity FROM " + TABLE_NAME + " WHERE id = ?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            int quantity = cursor.getInt(0);
            cursor.close();
            return quantity;
        }
        cursor.close();
        return 0;
    }

    @Override
    public boolean updateProductQuantity(int productId, int newQuantity) {
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        int rowsAffected = database.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(productId)});
        return rowsAffected > 0;
    }

    @Override
    public List<ReportLowStock> getLowStockProducts(int threshold) {
        List<ReportLowStock> lowStockList = new ArrayList<>();
        Cursor cursor = database.rawQuery(String.format("SELECT p.name, p.quantity FROM %s as p WHERE p.quantity < ? ORDER BY p.quantity ASC",TABLE_NAME), new String[]{String.valueOf(threshold)});
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int quantityIndex = cursor.getColumnIndex("quantity");
            if (nameIndex != -1 && quantityIndex != -1) {
                do {
                    String name = cursor.getString(nameIndex);
                    int quantity = cursor.getInt(quantityIndex);

                    lowStockList.add(new ReportLowStock(name, quantity));
                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHelper", "Column indices for 'name' or 'quantity' are invalid.");
            }
            cursor.close();
        } else {
            Log.e("DatabaseHelper", "Failed to retrieve data from products table.");
        }
        return lowStockList;
    }

    @Override
    public List<ReportStockSummary> getStockSummary() {
        List<ReportStockSummary> stockSummaryList = new ArrayList<>();
        Cursor cursor = database.rawQuery(String.format("SELECT p.name, p.quantity, p.price FROM %s AS p ORDER BY p.name ASC",TABLE_NAME), null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int priceIndex = cursor.getColumnIndex("price");
            if (nameIndex != -1 && quantityIndex != -1 && priceIndex != -1) {
                do {
                    String name = cursor.getString(nameIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    double price = cursor.getDouble(priceIndex);
                    stockSummaryList.add(new ReportStockSummary(name, quantity, price));
                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHelper", "Column indices for 'name', 'quantity' or 'price' are invalid.");
            }
            cursor.close();
        } else {
            Log.e("DatabaseHelper", "Failed to retrieve data from products table.");
        }
        return stockSummaryList;
    }

    private Product getProduct(Cursor cursor){
        int idIndex = cursor.getColumnIndex("id");
        int nameIndex = cursor.getColumnIndex("name");
        int qtyIndex = cursor.getColumnIndex("quantity");
        int priceIndex = cursor.getColumnIndex("price");
        int descIndex = cursor.getColumnIndex("description");
        int imgRefIndex = cursor.getColumnIndex("imageReference");
        if (idIndex != -1 && nameIndex != -1 && qtyIndex != -1 && priceIndex != -1 &&
                descIndex != -1 && imgRefIndex != -1) {
            return new Product(
                    cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getInt(qtyIndex),
                    cursor.getDouble(priceIndex),
                    cursor.getString(descIndex),
                    cursor.getString(imgRefIndex)
            );
        }else{
            throw new RuntimeException(new IndexOutOfBoundsException("One or more column indices are invalid."));
        }
    }
}
