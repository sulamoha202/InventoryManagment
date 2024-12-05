package com.mtsd.repository.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.Movement;
import com.mtsd.model.Product;
import com.mtsd.model.Revenue;
import com.mtsd.repository.MovementRepository;
import com.mtsd.repository.ProductRepository;
import com.mtsd.repository.RevenueRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MovementRepositoryImpl implements MovementRepository {

    private final SQLiteDatabase database;
    private final static String TABLE_NAME = "inventory_movements" ;

    public MovementRepositoryImpl(SQLiteDatabase database){
        this.database = database;
    }

    @Override
    public boolean insertMovement(Movement movement) {
        ContentValues values = new ContentValues();
        values.put("product_id", movement.getProductId());
        values.put("movement_type", movement.getMovementType());
        values.put("quantity", movement.getQuantity());
        values.put("date", movement.getDate());
        long result = database.insert(TABLE_NAME,null,values);
        if(result != -1) updateRevenue(movement);
        return result != -1;
    }

    @Override
    public boolean updateMovement(Movement movement) {
        ContentValues values = new ContentValues();
        values.put("product_id", movement.getProductId());
        values.put("movement_type", movement.getMovementType());
        values.put("quantity", movement.getQuantity());
        values.put("date", movement.getDate());
        int rowsAffected = database.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(movement.getId())});
        if(rowsAffected > 0) updateRevenue(movement);

        return rowsAffected > 0;
    }

    @Override
    public Movement getMovementById(int movementId) {
        Cursor cursor = database.query(TABLE_NAME, null, "id = ?", new String[]{String.valueOf(movementId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Movement movement = getMovement(cursor);
            cursor.close();
            return movement;
        }
        return null;
    }

    @Override
    public List<Movement> getAllMovements() {
        List<Movement> movements = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                movements.add(getMovement(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movements;
    }

    @Override
    public List<Movement> getLastMovements(int rowSize){
        List<Movement> movements = new ArrayList<>();
        String sql = "SELECT * FROM "+TABLE_NAME+" ORDER BY date DESC LIMIT "+ rowSize;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                movements.add(getMovement(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movements;
    }

    @Override
    public boolean deleteMovement(int movementId) {
        int rowsAffected = database.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(movementId)});
        return rowsAffected > 0;
    }


    private Movement getMovement(Cursor cursor){
        int idIndex = cursor.getColumnIndex("id");
        int productIdIndex = cursor.getColumnIndex("product_id");
        int moveTypeIndex = cursor.getColumnIndex("movement_type");
        int qtyIndex = cursor.getColumnIndex("quantity");
        int dateIndex = cursor.getColumnIndex("date");
        return new Movement(
                cursor.getInt(idIndex),
                cursor.getInt(productIdIndex),
                cursor.getString(moveTypeIndex),
                cursor.getInt(qtyIndex),
                cursor.getString(dateIndex)
        );
    }

    private void updateRevenue(Movement movement){
        RevenueRepository revenueRepository = new RepositoryManager(database).getRevenueRepository();
        double revenueChange = 0;
        if("SOLD".equals(movement.getMovementType())){
            double price = getPriceForProduct(movement.getProductId());
            revenueChange += price * movement.getQuantity();
        }
        if("BOUGHT".equals(movement.getMovementType())){
            double price = getPriceForProduct(movement.getProductId());
            revenueChange -= price * movement.getQuantity();
        }

        double currentRevenue = revenueRepository.getTotalRevenue();
        double newRevenue = currentRevenue + revenueChange;

        Revenue revenue = new Revenue(0,getCurrentDate(),currentRevenue);
        revenueRepository.updateRevenue(revenue);
    }
    private double getPriceForProduct(int productId) {
        ProductRepository productRepository = new  RepositoryManager(database).getProductRepository();
        Product product = productRepository.getProductById(productId);
        return product.getPrice();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
