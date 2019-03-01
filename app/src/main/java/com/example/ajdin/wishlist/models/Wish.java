package com.example.ajdin.wishlist.models;

import java.io.Serializable;

public class Wish implements Serializable {

    private int Id;
    private String Name;
    private Double Amount;
    private int UserId;

    public Wish(int id, String name, Double amount, int userId) {
        Id = id;
        Name = name;
        Amount = amount;
        UserId = userId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }
}
