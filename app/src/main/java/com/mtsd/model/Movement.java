package com.mtsd.model;

public class Movement {
    private int id;
    private String productName;
    private String movementType;
    private int quantity;
    private String date;

    public Movement(int id, String productName, String movementType, int quantity, String date) {
        this.productName = productName;
        this.movementType = movementType;
        this.quantity = quantity;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDate(String date) {
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
