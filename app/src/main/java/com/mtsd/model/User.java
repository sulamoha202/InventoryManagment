package com.mtsd.model;

import androidx.annotation.NonNull;

public class User {
    private int id;
    private String username;
    private String name;
    private String email;
    private String password;
    private int storeInfoId;

    public User() {
    }

    public User(int id, String username, String name, String email, String password, int storeInfo) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.storeInfoId = storeInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStoreInfoId() {
        return storeInfoId;
    }

    public void setStoreInfoId(int storeInfoId) {
        this.storeInfoId = storeInfoId;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", storeInfoId=" + storeInfoId +
                '}';
    }
}
