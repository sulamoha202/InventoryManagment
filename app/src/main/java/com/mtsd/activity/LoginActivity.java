package com.mtsd.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mtsd.model.StoreInfo;
import com.mtsd.util.DatabaseHelper;
import com.mtsd.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton;
    private TextView forgotPassword;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
            startActivity(intent);
            finish();
        }

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (validateUser(username, password)) {
                Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        forgotPassword.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Redirecting to password recovery...", Toast.LENGTH_SHORT).show());
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean isValid = isValid(username, password);

        if (isValid && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            String name = cursor.getString(nameIndex);
            int storeIdIndex = cursor.getColumnIndex("store_id");
            int storeId = cursor.getInt(storeIdIndex);

            // Query the store_info table to get the store name
            StoreInfo store = getStoreDataById(storeId);
            Log.d("SharedPReference","store data are: "+store.toString());
            // Save user details and store name in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.putString("name", name);
            editor.putString("store_name", store.getName());
            editor.putString("store_address", store.getAddress());
            editor.putString("store_phone", store.getPhone());
            editor.putString("store_email", store.getEmail());// Save the store name
            editor.putBoolean("isLoggedIn", true);
            editor.apply();
        }
        cursor.close();
        return isValid;
    }

    public boolean isValid(String username, String password) {
        return dbHelper.checkUserCredentials(username, password);
    }

    // Method to get store name by store ID
    private StoreInfo getStoreDataById(int storeId) {
        StoreInfo store = new StoreInfo();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM store_info WHERE id=?", new String[]{String.valueOf(storeId)});


        if (cursor != null && cursor.moveToFirst()) {
            int storeIdIndex = cursor.getColumnIndex("id");
            int storeNameIndex = cursor.getColumnIndex("name");
            int storeAddressIndex = cursor.getColumnIndex("address");
            int storePhoneIndex = cursor.getColumnIndex("phone");
            int storeEmailIndex = cursor.getColumnIndex("email");

            int id = cursor.getInt(storeIdIndex);
            String name = cursor.getString(storeNameIndex);
            String address = cursor.getString(storeAddressIndex);
            String phone = cursor.getString(storePhoneIndex);
            String email = cursor.getString(storeEmailIndex);
            store.setId(id);
            store.setName(name);
            store.setAddress(address);
            store.setPhone(phone);
            store.setEmail(email);
        }
        cursor.close();
        return store;
    }
}

