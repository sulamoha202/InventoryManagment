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
import com.mtsd.model.RevenueData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DDL_FILE_PATH = "sqlite/ddl.sql";

    // Table and Column Names
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_STORE_INFO = "store_info";


    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_REFERENCE = "imageReference";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    // Column Names
    private static final String COLUMN_STORE_ID = "id";
    private static final String COLUMN_STORE_NAME = "name";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";

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
    // Update user details in the database

    // Update store information in the database
    public void updateStoreInfo(String storeName, String address, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, storeName);
        contentValues.put(COLUMN_ADDRESS, address);
        contentValues.put(COLUMN_PHONE, phone);
        contentValues.put(COLUMN_EMAIL, email);

        // Update store information for the first (or only) row in the table
        db.update(TABLE_STORE_INFO, contentValues, null, null);  // Add WHERE clause if necessary
        db.close();
    }
    public Cursor getStoreById(int storeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STORE_INFO + " WHERE id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(storeId)});
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

    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowAffected = db.delete(TABLE_PRODUCTS, COLUMN_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
        return rowAffected > 0;
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
                // Validate column indices before accessing
                int idColumnIndex = cursor.getColumnIndex("id");
                int productNameColumnIndex = cursor.getColumnIndex("product_name");
                int movementTypeColumnIndex = cursor.getColumnIndex("movement_type");
                int quantityColumnIndex = cursor.getColumnIndex("quantity");
                int dateColumnIndex = cursor.getColumnIndex("date");

                // Check if any column index is invalid
                if (idColumnIndex != -1 && productNameColumnIndex != -1 && movementTypeColumnIndex != -1 &&
                        quantityColumnIndex != -1 && dateColumnIndex != -1) {
                    int id = cursor.getInt(idColumnIndex);
                    String productName = cursor.getString(productNameColumnIndex);
                    String movementType = cursor.getString(movementTypeColumnIndex);
                    int quantity = cursor.getInt(quantityColumnIndex);
                    String date = cursor.getString(dateColumnIndex);

                    movementList.add(new Movement(id, productName, movementType, quantity, date));
                } else {
                    Log.e("Database Error", "One or more columns are missing in the cursor.");
                }

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

    public List<RevenueData> getLastWeekRevenue() {
        List<RevenueData> revenueDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query for last week's revenue grouped by days
        String query = "SELECT strftime('%w', date) AS day, SUM(revenue) AS totalRevenue FROM revenue_table " +
                "WHERE date >= date('now', '-7 days') GROUP BY strftime('%w', date)";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            int dayIndex = cursor.getColumnIndex("day");
            int revenueIndex = cursor.getColumnIndex("totalRevenue");

            // Ensure valid indexes are found
            if (dayIndex != -1 && revenueIndex != -1) {
                do {
                    String day = cursor.getString(dayIndex);
                    float revenue = cursor.getFloat(revenueIndex);

                    String label = getDayLabel(day); // Convert day number to label (e.g., Sunday, Monday)
                    revenueDataList.add(new RevenueData(label, revenue));
                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHelper", "Column indices for 'day' or 'totalRevenue' are invalid.");
            }
            cursor.close();
        } else {
            Log.e("DatabaseHelper", "Failed to retrieve data from revenue_table.");
        }
        return revenueDataList;
    }

    public List<Movement> getLastFiveMovements() {
        List<Movement> movements = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM inventory_movements ORDER BY date DESC LIMIT 5";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // Get the indices of the columns in the Cursor
            int idColumnIndex = cursor.getColumnIndex("id");
            int productNameColumnIndex = cursor.getColumnIndex("product_id");  // Assuming "product_id" is the correct column
            int movementTypeColumnIndex = cursor.getColumnIndex("movement_type");
            int quantityColumnIndex = cursor.getColumnIndex("quantity");
            int dateColumnIndex = cursor.getColumnIndex("date");

            // Check if the columns exist (indices are valid)
            if (idColumnIndex != -1 && productNameColumnIndex != -1 && movementTypeColumnIndex != -1 &&
                    quantityColumnIndex != -1 && dateColumnIndex != -1) {

                do {
                    Movement movement = new Movement(
                            cursor.getInt(idColumnIndex),
                            cursor.getString(productNameColumnIndex),
                            cursor.getString(movementTypeColumnIndex),
                            cursor.getInt(quantityColumnIndex),
                            cursor.getString(dateColumnIndex)
                    );
                    movements.add(movement);
                } while (cursor.moveToNext());
            } else {
                Log.e("Database Error", "One or more columns are missing in the cursor.");
            }
        }

        Log.d("Cursor Count", String.valueOf(cursor.getCount()));  // Log number of rows returned
        cursor.close();
        db.close();
        return movements;
    }

    // Helper to convert day numbers to names
    private String getDayLabel(String dayNumber) {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[Integer.parseInt(dayNumber)];
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
                // Validate column indices before accessing
                int productNameColumnIndex = cursor.getColumnIndex("product_name");
                int movementTypeColumnIndex = cursor.getColumnIndex("movement_type");
                int quantityColumnIndex = cursor.getColumnIndex("quantity");
                int dateColumnIndex = cursor.getColumnIndex("date");

                // Check if any column index is invalid
                if (productNameColumnIndex != -1 && movementTypeColumnIndex != -1 &&
                        quantityColumnIndex != -1 && dateColumnIndex != -1) {
                    String productName = cursor.getString(productNameColumnIndex);
                    String movementType = cursor.getString(movementTypeColumnIndex);
                    int quantity = cursor.getInt(quantityColumnIndex);
                    String date = cursor.getString(dateColumnIndex);

                    movementList.add(new ReportRecentMovement(productName, movementType, quantity, date));
                } else {
                    Log.e("Database Error", "One or more columns are missing in the cursor.");
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        return movementList;
    }

    public List<ReportLowStock> getLowStockProducts(int threshold) {
        List<ReportLowStock> lowStockList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get low stock products
        Cursor cursor = db.rawQuery(
                "SELECT p.name, p.quantity " +
                        "FROM products p " +
                        "WHERE p.quantity < ? " +
                        "ORDER BY p.quantity ASC",
                new String[]{String.valueOf(threshold)});

        if (cursor != null && cursor.moveToFirst()) {
            // Get column indices
            int nameIndex = cursor.getColumnIndex("name");
            int quantityIndex = cursor.getColumnIndex("quantity");

            // Ensure valid column indices
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

    public List<ReportStockSummary> getStockSummary() {
        List<ReportStockSummary> stockSummaryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get the stock summary
        Cursor cursor = db.rawQuery(
                "SELECT p.name, p.quantity, p.price " +
                        "FROM products p " +
                        "ORDER BY p.name ASC", null);

        if (cursor != null && cursor.moveToFirst()) {
            // Get column indices
            int nameIndex = cursor.getColumnIndex("name");
            int quantityIndex = cursor.getColumnIndex("quantity");
            int priceIndex = cursor.getColumnIndex("price");

            // Ensure valid column indices
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

    private void insertDefaultData() {
        // Insert electronic products into the database
        insertProduct("Samsung Galaxy S24 Ultra", 4, 1600.11, "Cell Phone", "");
        insertProduct("Apple iPhone 16 Pro Max", 2, 1499.99, "Cell Phone", "");
        insertProduct("Xiaomi Realme C53", 15, 149.99, "Cell Phone", "");
        insertProduct("Google Pixel 8", 10, 799.99, "Cell Phone", "");
        insertProduct("OnePlus 11", 5, 899.00, "Cell Phone", "");
        insertProduct("Sony Xperia 1 IV", 3, 1200.00, "Cell Phone", "");
        insertProduct("Apple MacBook Air", 3, 1899.99, "Laptop", "");
        insertProduct("Lenovo Ideapad Series 3", 8, 850.00, "Laptop", "");
        insertProduct("Dell Alienware", 2, 1450.00, "Laptop", "");
        insertProduct("HP Spectre x360", 7, 1399.00, "Laptop", "");
        insertProduct("Asus ROG Zephyrus G14", 4, 1600.00, "Laptop", "");
        insertProduct("Microsoft Surface Laptop 5", 5, 1300.00, "Laptop", "");
        insertProduct("Razer Blade 15", 3, 1799.00, "Laptop", "");
        insertProduct("Apple iPad Pro 12.9", 12, 1099.00, "Tablet", "");
        insertProduct("Samsung Galaxy Tab S8", 10, 849.99, "Tablet", "");
        insertProduct("Bose QuietComfort 45", 10, 329.99, "Headphones", "");
        insertProduct("Apple AirPods Pro 2", 18, 249.99, "Headphones", "");
        insertProduct("Logitech MX Master 3", 22, 99.99, "Mouse", "");
        insertProduct("Samsung Odyssey G7", 7, 799.99, "Monitor", "");
        insertProduct("Corsair K95 RGB Platinum", 11, 199.99, "Keyboard", "");


        addInventoryMovement(1, "ADD", 10, "2024-11-24");
        addInventoryMovement(2, "REMOVE", 1, "2024-11-24");
        addInventoryMovement(3, "ADD", 5, "2024-11-23");
        addInventoryMovement(4, "REMOVE", 2, "2024-11-23");
        addInventoryMovement(5, "ADD", 3, "2024-11-22");
        addInventoryMovement(6, "REMOVE", 1, "2024-11-22");
        addInventoryMovement(7, "ADD", 8, "2024-11-21");
        addInventoryMovement(8, "REMOVE", 4, "2024-11-21");
        addInventoryMovement(9, "ADD", 2, "2024-11-20");
        addInventoryMovement(10, "REMOVE", 3, "2024-11-20");
        addInventoryMovement(11, "ADD", 15, "2024-11-19");
        addInventoryMovement(12, "REMOVE", 10, "2024-11-19");
        addInventoryMovement(13, "ADD", 6, "2024-11-18");
        addInventoryMovement(14, "REMOVE", 7, "2024-11-18");
        addInventoryMovement(15, "ADD", 4, "2024-11-17");
        addInventoryMovement(16, "REMOVE", 5, "2024-11-17");
        addInventoryMovement(17, "ADD", 12, "2024-11-16");
        addInventoryMovement(18, "REMOVE", 2, "2024-11-16");
        addInventoryMovement(19, "ADD", 9, "2024-11-15");
        addInventoryMovement(20, "REMOVE", 3, "2024-11-15");

        Log.d("DatabaseHelper", "Default data inserted.");
    }

    private Product createProductFromCursor(Cursor cursor) {
        Product product = new Product();

        // Get column indices
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
        int imageReferenceIndex = cursor.getColumnIndex(COLUMN_IMAGE_REFERENCE);

        // Ensure valid column indices
        if (idIndex != -1 && nameIndex != -1 && quantityIndex != -1 && priceIndex != -1 &&
                descriptionIndex != -1 && imageReferenceIndex != -1) {

            // Set product attributes
            product.setId(cursor.getInt(idIndex));
            product.setName(cursor.getString(nameIndex));
            product.setQuantity(cursor.getInt(quantityIndex));
            product.setPrice(cursor.getDouble(priceIndex));
            product.setDescription(cursor.getString(descriptionIndex));
            product.setImageReference(cursor.getString(imageReferenceIndex));

        } else {
            Log.e("DatabaseHelper", "One or more column indices are invalid.");
        }

        return product;
    }


}
