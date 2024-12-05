package com.mtsd.repository.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mtsd.model.Movement;
import com.mtsd.model.Revenue;
import com.mtsd.repository.RevenueRepository;

public class RevenueRepositoryImpl implements RevenueRepository {

    private final static String TABLE_NAME = "revenue_table";
    private final SQLiteDatabase database;


    public RevenueRepositoryImpl(SQLiteDatabase database){
        this.database = database;
    }

    @Override
    public Revenue getRevenueByDate(String date){
        Cursor cursor = database.query(TABLE_NAME, null, "date = ?", new String[]{String.valueOf(date)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int dateIndex = cursor.getColumnIndex("date");
            int revenueIndex = cursor.getColumnIndex("revenue");
            Revenue revenue = new Revenue(cursor.getInt(idIndex),cursor.getString(dateIndex),cursor.getDouble(revenueIndex));
            cursor.close();
            return revenue;
        }
        return null;
    }


    @Override
    public void updateRevenue(Revenue revenue){
        ContentValues values = new ContentValues();
        values.put("date",revenue.getDate());
        values.put("revenue",revenue.getRevenue());

        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_NAME+ " WHERE date = ?",new String[]{revenue.getDate()});
        if(cursor != null && cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex("id");
            database.update(TABLE_NAME,values,"id = ?",new String[]{String.valueOf(cursor.getInt(idIndex))});
        }else{
            database.insert(TABLE_NAME,null,values);
        }
        cursor.close();
    }

    @Override
    public double getTotalRevenue(){
        double totalRevenue = 0;
        Cursor cursor = database.rawQuery("SELECT revenue FROM "+ TABLE_NAME, null);
        if(cursor != null && cursor.moveToFirst()){
            do{
                int revenueIndex = cursor.getColumnIndex("revenue");
                totalRevenue += cursor.getDouble(revenueIndex);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return totalRevenue;
    }

}
