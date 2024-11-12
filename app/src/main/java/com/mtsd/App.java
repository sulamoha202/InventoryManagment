package com.mtsd;

import android.app.Application;
import android.util.Log;

import com.mtsd.util.DatabaseHelper;

public class App extends Application {
    private static App instance;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;

        // Initialize database helper
        initializeDatabase();
        databaseHelper.initializeDefaultData();

        // Additional initializations can go here (e.g., logging, analytics)
        Log.d("App", "App initialized with DatabaseHelper and other dependencies.");
    }

    private void initializeDatabase() {
        databaseHelper = new DatabaseHelper(this);
        Log.d("App", "DatabaseHelper initialized.");
    }

    public static App getInstance() {
        return instance;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
