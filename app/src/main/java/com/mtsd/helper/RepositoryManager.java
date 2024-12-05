package com.mtsd.helper;

import android.database.sqlite.SQLiteDatabase;

import com.mtsd.repository.MovementRepository;
import com.mtsd.repository.ProductRepository;
import com.mtsd.repository.RevenueRepository;
import com.mtsd.repository.StoreInfoRepository;
import com.mtsd.repository.UserRepository;
import com.mtsd.repository.impl.MovementRepositoryImpl;
import com.mtsd.repository.impl.ProductRepositoryImpl;
import com.mtsd.repository.impl.RevenueRepositoryImpl;
import com.mtsd.repository.impl.StoreInfoRepositoryImpl;
import com.mtsd.repository.impl.UserRepositoryImpl;

public class RepositoryManager {
    private final SQLiteDatabase database;

    public RepositoryManager(SQLiteDatabase database){
        this.database = database;
    }
    public ProductRepository getProductRepository(){
        return new ProductRepositoryImpl(database);
    }
    public UserRepository getUserRepository(){
        return new UserRepositoryImpl(database);
    }
    public MovementRepository getMovementRepository(){return new MovementRepositoryImpl(database);}
    public RevenueRepository getRevenueRepository(){return new RevenueRepositoryImpl(database);}
    public StoreInfoRepository getStoreInfoRepository(){return new StoreInfoRepositoryImpl(database);}

}
