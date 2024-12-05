package com.mtsd.repository.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mtsd.model.StoreInfo;
import com.mtsd.model.User;
import com.mtsd.repository.StoreInfoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoreInfoRepositoryImpl implements StoreInfoRepository {

    private final SQLiteDatabase database;
    private final static String TABLE_NAME = "store_info" ;

    public StoreInfoRepositoryImpl(SQLiteDatabase database){
        this.database = database;
    }


    @Override
    public boolean insert(StoreInfo storeInfo) {
        ContentValues values = new ContentValues();
        values.put("name", storeInfo.getName());
        values.put("email", storeInfo.getEmail());
        values.put("address", storeInfo.getAddress());
        values.put("phone", storeInfo.getPhone());
        long result = database.insert(TABLE_NAME,null,values);
        return result != -1;
    }

    @Override
    public boolean update(StoreInfo storeInfo) {
        ContentValues values = new ContentValues();
        values.put("name", storeInfo.getName());
        values.put("email", storeInfo.getEmail());
        values.put("address", storeInfo.getAddress());
        values.put("phone", storeInfo.getPhone());
        int rowsAffected = database.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(storeInfo.getId())});
        return rowsAffected > 0;
    }

    @Override
    public boolean delete(int storeInfoId) {
        int rowsAffected = database.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(storeInfoId)});
        return rowsAffected > 0;
    }

    @Override
    public StoreInfo getById(int storeInfoId) {
        Cursor cursor = database.query(TABLE_NAME, null, "id = ?", new String[]{String.valueOf(storeInfoId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            StoreInfo user = getStoreInfo(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    @Override
    public List<StoreInfo> getAll() {
        List<StoreInfo> storesInfo = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                storesInfo.add(getStoreInfo(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return storesInfo;
    }

    private StoreInfo getStoreInfo(Cursor cursor){
        int idIndex = cursor.getColumnIndex("id");
        int nameIndex = cursor.getColumnIndex("name");
        int emailIndex = cursor.getColumnIndex("email");
        int addressIndex = cursor.getColumnIndex("address");
        int phoneIndex = cursor.getColumnIndex("phone");
        return new StoreInfo(
                cursor.getInt(idIndex),
                cursor.getString(nameIndex),
                cursor.getString(addressIndex),
                cursor.getString(phoneIndex),
                cursor.getString(emailIndex)
        );
    }
}
