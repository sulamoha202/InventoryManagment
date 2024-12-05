package com.mtsd.model;

public class Revenue {

    private int id;
    private String date;
    private double revenue;

    public Revenue() {
    }

    public Revenue(int id, String date, double revenue) {
        this.id = id;
        this.date = date;
        this.revenue = revenue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "Revenue{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", revenue=" + revenue +
                '}';
    }
}
