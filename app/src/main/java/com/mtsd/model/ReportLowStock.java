package com.mtsd.model;

public class ReportLowStock {
    private String productName;
    private int quantity;

    public ReportLowStock(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}
