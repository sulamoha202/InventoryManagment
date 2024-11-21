package com.mtsd.model;

public class ReportRecentMovement {
    private String productName;
    private String movementType;
    private int quantity;
    private String date;

    public ReportRecentMovement(String productName, String movementType, int quantity, String date) {
        this.productName = productName;
        this.movementType = movementType;
        this.quantity = quantity;
        this.date = date;
    }

    public String getProductName() {
        return productName;
    }

    public String getMovementType() {
        return movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }
}


