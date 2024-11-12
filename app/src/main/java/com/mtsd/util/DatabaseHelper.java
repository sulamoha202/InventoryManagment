package com.mtsd.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mtsd.model.Product;

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(DDL_FILE_PATH)))) {
            StringBuilder statement = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                if (line.trim().endsWith(";")) {
                    db.execSQL(statement.toString().trim());
                    statement = new StringBuilder();
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
            //insertDefaultData();
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

    private void insertDefaultData() {
        insertUser("admin", "admin");
        insertProduct("Samsung galaxy S24 Ultra", 4, 1600.11, "It a Cell Phone", "");
        insertProduct("Apple Iphone 16 Pro Max", 2, 1499.99, "It a Cell Phone", "");
        insertProduct("Apple Macbook Air", 3, 1899.99, "It a Laptop", "");
        insertProduct("Lenovo Ideapad Series 3", 8, 850.00, "It a Laptop", "");
        insertProduct("Dell Alienware", 2, 1450.00, "It a Laptop", "");
        insertProduct("Xiaomi Realme C53", 15, 149.99, "It a Cell Phone", "");
        Log.d("DatabaseHelper", "Default data inserted.");
    }

    private void insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COLUMN_USERNAME + ", " + COLUMN_PASSWORD + ") VALUES (?, ?)",
                new Object[]{username, password});
        Log.d("DatabaseHelper", "User inserted: " + username);
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
