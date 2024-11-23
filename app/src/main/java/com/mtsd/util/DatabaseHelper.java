package com.mtsd.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mtsd.model.Movement;
import com.mtsd.model.Product;
import com.mtsd.model.ReportLowStock;
import com.mtsd.model.ReportRecentMovement;
import com.mtsd.model.ReportStockSummary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DDL_FILE_PATH = "sqlite/ddl.sql";

    // Table and Column Names
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_REFERENCE = "imageReference";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            executeSQLScript(db);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error during onCreate", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS store_info");
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                products.add(createProductFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    private void executeSQLScript(SQLiteDatabase db) {
        Log.i("DatabaseHelper", "Execute sql script started");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(DDL_FILE_PATH)))) {
            StringBuilder statement = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                if (line.trim().endsWith(";")) { // SQL statement ends
                    String sql = statement.toString().trim();
                    try {
                        db.execSQL(sql);
                        Log.i("DatabaseHelper", "Executed successfully: " + sql);
                    } catch (SQLException e) {
                        Log.e("DatabaseHelper", "Execution failed for: " + sql, e);
                    }
                    statement = new StringBuilder(); // Reset for the next statement
                }
            }
        } catch (IOException e) {
            Log.e("DatabaseHelper", "Error reading SQL script", e);
        }
    }


    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public boolean insertProduct(String name, int quantity, double price, String description, String imageReference) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createProductContentValues(name, quantity, price, description, imageReference);
        long result = db.insert(TABLE_PRODUCTS, null, values);
        return result != -1;
    }

    private ContentValues createProductContentValues(String name, int quantity, double price, String description, String imageReference) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_IMAGE_REFERENCE, imageReference);
        return values;
    }

    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, COLUMN_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Product product = null;
        if (cursor != null && cursor.moveToFirst()) {
            product = createProductFromCursor(cursor);
            cursor.close();
        }
        return product;
    }

    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createProductContentValues(product.getName(), product.getQuantity(), product.getPrice(), product.getDescription(), product.getImageReference());

        int rowsAffected = db.update(TABLE_PRODUCTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(product.getId())});
        db.close();
        return rowsAffected > 0;
    }

    public void initializeDefaultData() {
        if (isProductsTableEmpty()) {
            insertDefaultData();
        } else {
            Log.d("DatabaseHelper", "Default data already exists.");
        }
    }

    private boolean isProductsTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }
    public List<Movement> getAllMovements() {
        List<Movement> movementList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT m.id, p.name AS product_name, m.movement_type, m.quantity, m.date " +
                        "FROM inventory_movements m " +
                        "JOIN products p ON m.product_id = p.id " +
                        "ORDER BY m.date DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(cursor.getColumnIndex("product_name"));
                String movementType = cursor.getString(cursor.getColumnIndex("movement_type"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                Movement movement = new Movement(productName, movementType, quantity, date);
                movementList.add(movement);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movementList;
    }

    public List<String> getProductNames() {
        List<String> productNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM products", null);
        if (cursor.moveToFirst()) {
            do {
                productNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productNames;
    }

    public int getProductIdByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM products WHERE name = ?", new String[]{productName});
        if (cursor.moveToFirst()) {
            int productId = cursor.getInt(0);
            cursor.close();
            return productId;
        }
        cursor.close();
        return -1; // Not found
    }

    public  int getQuantitySumOfAllProducts(){
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(quantity) FROM products";
        Cursor cursor = db.rawQuery(query,null);

        int totalQuantity = 0;
        if(cursor.moveToFirst()){
            totalQuantity = cursor.getInt(0);
        }
        cursor.close();
        return totalQuantity;
    }

    public  int getLowStockCount(int threshold){
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(quantity) FROM products WHERE quantity <= ?";
        Cursor cursor = db.rawQuery(query,new String[]{String.valueOf(threshold)});

        int count = 0;
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public boolean addInventoryMovement(int productId, String movementType, int quantity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("movement_type", movementType);
        values.put("quantity", quantity);
        values.put("date", date);
        long result = db.insert("inventory_movements", null, values);
        return result != -1;
    }
    public int getProductQuantityById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quantity FROM products WHERE id = ?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            int quantity = cursor.getInt(0);
            cursor.close();
            return quantity;
        }
        cursor.close();
        return 0; // Default to 0 if product not found
    }

    public double getTotalInventoryValue(){
        SQLiteDatabase db = this.getReadableDatabase();
        double totalValue =0;
        Cursor cursor = db.rawQuery("SELECT SUM(price * quantity) AS total FROM products",null);

        if(cursor.moveToFirst()){
            totalValue = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        return totalValue;
    }
    public boolean updateProductQuantity(int productId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        int rowsAffected = db.update("products", values, "id = ?", new String[]{String.valueOf(productId)});
        return rowsAffected > 0;
    }
    public List<ReportRecentMovement> getRecentMovements() {
        List<ReportRecentMovement> movementList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT m.id, p.name AS product_name, m.movement_type, m.quantity, m.date " +
                        "FROM inventory_movements m " +
                        "JOIN products p ON m.product_id = p.id " +
                        "ORDER BY m.date DESC " +
                        "LIMIT 10", null);

        if (cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(cursor.getColumnIndex("product_name"));
                String movementType = cursor.getString(cursor.getColumnIndex("movement_type"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                movementList.add(new ReportRecentMovement(productName, movementType, quantity, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movementList;
    }
    public List<ReportLowStock> getLowStockProducts(int threshold) {
        List<ReportLowStock> lowStockList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT p.name, p.quantity " +
                        "FROM products p " +
                        "WHERE p.quantity < ? " +
                        "ORDER BY p.quantity ASC", new String[]{String.valueOf(threshold)});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));

                lowStockList.add(new ReportLowStock(name, quantity));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lowStockList;
    }



    public List<ReportStockSummary> getStockSummary() {
        List<ReportStockSummary> stockSummaryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT p.name, p.quantity, p.price " +
                        "FROM products p " +
                        "ORDER BY p.name ASC", null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndex("price"));

                stockSummaryList.add(new ReportStockSummary(name, quantity, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stockSummaryList;
    }




    private void insertDefaultData() {
        insertProduct("Samsung galaxy S24 Ultra", 4, 1600.11, "It a Cell Phone", "");
        insertProduct("Apple Iphone 16 Pro Max", 2, 1499.99, "It a Cell Phone", "");
        insertProduct("Apple Macbook Air", 3, 1899.99, "It a Laptop", "");
        insertProduct("Lenovo Ideapad Series 3", 8, 850.00, "It a Laptop", "");
        insertProduct("Dell Alienware", 2, 1450.00, "It a Laptop", "");
        insertProduct("Xiaomi Realme C53", 15, 149.99, "It a Cell Phone", "");
        Log.d("DatabaseHelper", "Default data inserted.");
    }

    private Product createProductFromCursor(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        product.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        product.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
        product.setPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
        product.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        product.setImageReference(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_REFERENCE)));
        return product;
    }
}
