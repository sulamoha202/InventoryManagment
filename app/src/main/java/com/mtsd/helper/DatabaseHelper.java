package com.mtsd.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mtsd.model.Movement;
import com.mtsd.model.Product;
import com.mtsd.model.RevenueData;
import com.mtsd.repository.MovementRepository;
import com.mtsd.repository.ProductRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DDL_FILE_PATH = "sqlite/ddl.sql";

    // Table and Column Names
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_STORE_INFO = "store_info";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";
    private final Context context;

    private RepositoryManager repositoryManager;




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        repositoryManager = new RepositoryManager(db);
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

    public SQLiteDatabase getReadableDatabaseInstance() {
        return this.getReadableDatabase();
    }

    public SQLiteDatabase getWritableDatabaseInstance() {
        return this.getWritableDatabase();
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS store_info");
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
    public List<RevenueData> getLastWeekRevenue() {
        List<RevenueData> revenueDataList = new ArrayList<>();
        ProductRepository productRepository = repositoryManager.getProductRepository();

        // Query for last week's revenue grouped by days
        String query = "SELECT strftime('%w', date) AS day, SUM(revenue) AS totalRevenue FROM revenue_table " +
                "WHERE date >= date('now', '-7 days') GROUP BY strftime('%w', date)";
        Cursor cursor = null ;//db.rawQuery(query, null);

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
    private String getDayLabel(String dayNumber) {
        String[] days = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
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
    private void insertDefaultData() {
        ProductRepository productRepository = repositoryManager.getProductRepository();
        MovementRepository movementRepository = repositoryManager.getMovementRepository();
        for(int i = 0; i < 20; i++){
           productRepository.insert(new Product(0,"Producto "+(i+1),(int)(Math.round(Math.random()*15+1)),(Math.random()*2000+1),"Descripcion del producto "+(i+1),""));
           movementRepository.insertMovement(new Movement(0,
                       (i+1),
                       (int)(Math.round(Math.random()*2)) == 1 ? "COMPRADO" : "VENDIDO",
                       (int)(Math.round(Math.random()*15+1)),
                       String.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(
                               new java.util.Date(
                                       ThreadLocalRandom.current().nextLong(
                                               new java.util.GregorianCalendar(2024, Calendar.OCTOBER, 1).getTimeInMillis(),
                                               System.currentTimeMillis()
                                       )
                               )
                       ))
                   ));
       }
        Log.d("DatabaseHelper", "Default data inserted.");
    }

    public RepositoryManager getRepositoryManager(){
        return repositoryManager;
    }


}
