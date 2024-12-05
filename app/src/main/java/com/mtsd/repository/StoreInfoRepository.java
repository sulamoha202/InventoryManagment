package com.mtsd.repository;

import com.mtsd.model.StoreInfo;
import com.mtsd.model.User;

import java.util.List;

public interface StoreInfoRepository {
    boolean insert(StoreInfo storeInfo);
    boolean update(StoreInfo storeInfo);
    boolean delete(int storeInfoId);
    StoreInfo getById(int storeInfoId);
    List<StoreInfo> getAll();
}
