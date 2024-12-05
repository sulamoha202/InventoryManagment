package com.mtsd.repository.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mtsd.model.User;
import com.mtsd.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private final SQLiteDatabase database;
    private final static String TABLE_NAME = "users" ;

    public UserRepositoryImpl(SQLiteDatabase database){
        this.database = database;
    }

    @Override
    public boolean insert(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("store_id", user.getStoreInfoId());
        long result = database.insert(TABLE_NAME,null,values);
        return result != -1;
    }

    @Override
    public boolean update(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("password", user.getPassword());
        values.put("store_id", user.getStoreInfoId());
        int rowsAffected = database.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(user.getId())});
        return rowsAffected > 0;
    }

    @Override
    public User getById(int userId) {
        Cursor cursor = database.query(TABLE_NAME, null, "id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = getUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    @Override
    public User getByUsername(String username) {
        Cursor cursor = database.query(TABLE_NAME, null, "username = ?", new String[]{String.valueOf(username)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = getUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                users.add(getUser(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    @Override
    public boolean delete(int userId) {
        int rowsAffected = database.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    @Override
    public boolean checkUserCredentials(User user) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE  username = ? AND password = ?";
        Cursor cursor = database.rawQuery(query, new String[]{user.getUsername(), user.getPassword()});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }


    private User getUser(Cursor cursor){
        int idIndex = cursor.getColumnIndex("id");
        int userNameIndex = cursor.getColumnIndex("username");
        int nameIndex = cursor.getColumnIndex("name");
        int emailIndex = cursor.getColumnIndex("email");
        int passwordIndex = cursor.getColumnIndex("password");
        int storeInfoIndex = cursor.getColumnIndex("store_id");

        if (idIndex == -1 || userNameIndex == -1 || nameIndex == -1 || emailIndex == -1 ||
                passwordIndex == -1 || storeInfoIndex == -1) {
            Log.e("DatabaseError", "One or more columns not found in cursor.");
            return null;
        }

        return new User(
                cursor.getInt(idIndex),
                cursor.getString(userNameIndex),
                cursor.getString(nameIndex),
                cursor.getString(emailIndex),
                cursor.getString(passwordIndex),
                cursor.getInt(storeInfoIndex)
        );
    }
}
