package com.example.ajdin.wishlist.models;

import java.util.Date;

public class Payment {

    private int Id;
    private int WishId;
    private Date Date;
    private Double Amount;

    public Payment(int id, int wishId, java.util.Date date, Double amount) {
        Id = id;
        WishId = wishId;
        Date = date;
        Amount = amount;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getWishId() {
        return WishId;
    }

    public void setWishId(int wishId) {
        WishId = wishId;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }
}
