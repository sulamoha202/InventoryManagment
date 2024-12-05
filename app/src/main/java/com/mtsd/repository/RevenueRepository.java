package com.mtsd.repository;

import com.mtsd.model.Revenue;
import com.mtsd.model.User;

import java.util.List;

public interface RevenueRepository {

    void updateRevenue(Revenue revenue);
    double getTotalRevenue();
    Revenue getRevenueByDate(String date);
}
