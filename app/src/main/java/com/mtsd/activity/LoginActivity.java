package com.mtsd.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mtsd.util.DatabaseHelper;
import com.mtsd.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField,passwordField;
    private Button loginButton;
    private TextView forgotPassword;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();

        sharedPreferences = getSharedPreferences("UserPrefs",MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);

        if(isLoggedIn){
            Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
            startActivity(intent);
            finish();
        }

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if(validateUser(username,password)){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username",username);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"Invalid username or password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgotPassword.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Redirecting to password recovery...", Toast.LENGTH_SHORT).show());
    }

    public boolean validateUser(String username, String passowrd){
        return dbHelper.checkUserCredentials(username,passowrd);
    }
    public EditText getUsernameField() {
        return usernameField;
    }

    public void setUsernameField(EditText usernameField) {
        this.usernameField = usernameField;
    }

    public EditText getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(EditText passwordField) {
        this.passwordField = passwordField;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void setLoginButton(Button loginButton) {
        this.loginButton = loginButton;
    }

    public TextView getForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(TextView forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

}
