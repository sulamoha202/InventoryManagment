package com.mtsd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.mtsd.util.DatabaseHelper;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        dbHelper.close();
        database.close();
    }

    @Test
    public void testDatabaseCreation() {
        // Test if the database file is created
        assertNotNull(database);
        Log.d("DatabaseHelperTest", "Database created successfully.");
    }

    @Test
    public void testExecuteSQLScript() {
        // Check if 'users' table exists after the script is executed
        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'", null);
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        assertTrue("The 'users' table should exist in the database.", tableExists);
        Log.d("DatabaseHelperTest", "Users table exists: " + tableExists);
    }

    @Test
    public void testChekUserCredentials_ValidUser() {
        // Insert a test user into the database
        String username = "admin";
        String password = "admin";
        database.execSQL("INSERT INTO users (username, password) VALUES (?, ?)", new Object[]{username, password});

        // Test the method with valid credentials
        boolean result = dbHelper.chekUserCredentials(username, password);
        assertTrue("Valid credentials should return true.", result);
        Log.d("DatabaseHelperTest", "Valid credentials result: " + result);
    }

    @Test
    public void testChekUserCredentials_InvalidUser() {
        // Test the method with invalid credentials
        boolean result = dbHelper.chekUserCredentials("nonExistentUser", "wrongPassword");
        assertFalse("Invalid credentials should return false.", result);
        Log.d("DatabaseHelperTest", "Invalid credentials result: " + result);
    }
}
