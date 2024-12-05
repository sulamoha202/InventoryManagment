package com.mtsd.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.StoreInfo;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.R;
import com.mtsd.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton;
    private TextView forgotPassword;
    private SharedPreferences sharedPreferences;
    private RepositoryManager repositoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SQLiteDatabase database = new DatabaseHelper(getApplicationContext()).getWritableDatabaseInstance();
        repositoryManager = new RepositoryManager(database);

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
        User user = repositoryManager.getUserRepository().getByUsername(username);

        if(user == null || !isValid(user)){
            Log.e("validateUser", "Invalid user or user not found.");
            return false;
        }

        if (!user.getPassword().equals(password)) {
            Log.e("validateUser", "Incorrect password.");
            return false;
        }

        StoreInfo store = repositoryManager.getStoreInfoRepository().getById(user.getStoreInfoId());
        if(store == null){
            Log.e("validateUser","Store information not found for user.");
            return false;
        }
        Log.d("validateUser","Store data: "+ store.toString());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user.getUsername());
        editor.putString("name", user.getName());

        editor.putString("store_name", store.getName());
        editor.putString("store_address", store.getAddress());
        editor.putString("store_phone", store.getPhone());
        editor.putString("store_email", store.getEmail());
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        Log.i("validateUser","User validation successful and data saved in SharedPreference.");
        return true;
    }

    public boolean isValid(User user) {
        return repositoryManager.getUserRepository().checkUserCredentials(user);
    }

}

