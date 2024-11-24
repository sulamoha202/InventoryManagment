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

import com.mtsd.model.Product;
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
    public void testCheckUserCredentials_ValidUser() {
        // Insert a test user into the database
        String username = "admin";
        String password = "admin";
        database.execSQL("INSERT INTO users (username, password) VALUES (?, ?)", new Object[]{username, password});

        // Test the method with valid credentials
        boolean result = dbHelper.checkUserCredentials(username, password);
        assertTrue("Valid credentials should return true.", result);
        Log.d("DatabaseHelperTest", "Valid credentials result: " + result);
    }

    @Test
    public void testCheckUserCredentials_InvalidUser() {
        // Test the method with invalid credentials
        boolean result = dbHelper.checkUserCredentials("nonExistentUser", "wrongPassword");
        assertFalse("Invalid credentials should return false.", result);
        Log.d("DatabaseHelperTest", "Invalid credentials result: " + result);
    }

    @Test
    public void testInsertProduct() {
        // Test inserting a product into the database
        String productName = "Test Product";
        int quantity = 10;
        double price = 99.99;
        String description = "Test product description.";

        // Attempt to insert the product
        boolean result = dbHelper.insertProduct(productName, quantity, price, description, "");

        // Assert that the insert was successful (assuming the method returns true on success)
        assertTrue("The product should be inserted into the database.", result);
        Log.d("DatabaseHelperTest", "Product inserted successfully.");
    }

    @Test
    public void testGetProductById() {
        // Insert a test product
        String productName = "Test Product";
        int quantity = 10;
        double price = 99.99;
        String description = "Test product description.";
        boolean insertionSuccessful = dbHelper.insertProduct(productName, quantity, price, description, "");

        // Ensure the product was inserted successfully
        assertTrue("Product should be inserted successfully.", insertionSuccessful);

        // Retrieve the product ID by name
        int productId = dbHelper.getProductIdByName(productName);

        // Ensure the product ID is valid
        assertNotEquals("Product ID should be greater than 0.", -1, productId);

        // Retrieve the product by ID
        Cursor cursor = (Cursor) dbHelper.getProductById(productId);
        assertNotNull("Product should be retrieved successfully by ID.", cursor);
        assertTrue("Product should exist in the cursor.", cursor.moveToFirst());

        // Verify the retrieved product data
        String retrievedName = cursor.getString(cursor.getColumnIndex("name"));
        assertEquals("Product name should match the inserted value.", productName, retrievedName);

        cursor.close();
    }


    @Test
    public void testUpdateProduct() {
        // Insert a test product
        String productName = "Test Product";
        int quantity = 10;
        double price = 99.99;
        String description = "Test product description.";
        boolean insertionSuccessful = dbHelper.insertProduct(productName, quantity, price, description, "");

        // Ensure the product was inserted successfully
        assertTrue("Product should be inserted successfully.", insertionSuccessful);

        // Retrieve the product ID by name
        int productId = dbHelper.getProductIdByName(productName);
        assertNotEquals("Product ID should be greater than 0.", -1, productId);

        // Create a Product object with updated details
        String updatedName = "Updated Test Product";
        int updatedQuantity = 20;
        double updatedPrice = 79.99;
        String updatedDescription = "Updated product description.";
        Product updatedProduct = new Product(productId, updatedName, updatedQuantity, updatedPrice, updatedDescription,"");

        // Update the product using the Product object
        boolean updateResult = dbHelper.updateProduct(updatedProduct);
         productId = dbHelper.getProductIdByName(productName);
        // Ensure the product was updated successfully
        assertTrue("Product should be updated successfully.", updateResult);

        // Retrieve the updated product by ID
        Cursor cursor = (Cursor) dbHelper.getProductById(productId);
        assertNotNull("Product should be retrieved successfully by ID.", cursor);
        assertTrue("Product should exist in the cursor.", cursor.moveToFirst());

        // Verify the updated product details
        String actualUpdatedName = cursor.getString(cursor.getColumnIndex("name"));
        assertEquals("Product name should be updated.", updatedName, actualUpdatedName);

        String actualUpdatedDescription = cursor.getString(cursor.getColumnIndex("description"));
        assertEquals("Product description should be updated.", updatedDescription, actualUpdatedDescription);

        cursor.close();
    }



    @Test
    public void testDeleteProduct() {
        // Insert a test product
        String productName = "Test Product";
        int quantity = 10;
        double price = 99.99;
        String description = "Test product description.";
        boolean isInserted = dbHelper.insertProduct(productName, quantity, price, description,"");

        int productId = dbHelper.getProductIdByName(productName);

        // Ensure the product was inserted successfully (productId should be > 0)
        assertTrue("Product should be inserted successfully.", isInserted);

        // Delete the product
        boolean deleteResult = dbHelper.deleteProduct(productId);

        // Ensure the product was deleted successfully
        assertTrue("Product should be deleted successfully.", deleteResult);

        // Verify the product is deleted by querying the database
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(productId)});
        assertFalse("Product should be deleted from the database.", cursor.moveToFirst());
        cursor.close();
    }


    @Test
    public void testGetAllProducts() {
        // Insert some test products
        dbHelper.insertProduct("Product 1", 10, 99.99, "Description 1","");
        dbHelper.insertProduct("Product 2", 5, 59.99, "Description 2","");

        // Test retrieving all products
        Cursor cursor = (Cursor) dbHelper.getAllProducts();
        assertTrue("There should be at least 2 products.", cursor.getCount() >= 2);
        cursor.close();
    }

}
