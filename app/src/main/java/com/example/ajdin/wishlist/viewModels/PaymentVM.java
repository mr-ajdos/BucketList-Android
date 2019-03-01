package com.example.ajdin.wishlist.viewModels;

import java.io.Serializable;
import java.util.Date;

public class PaymentVM implements Serializable {

    public int Id;
    public String CreatedDate;
    public Double Amount;

    public PaymentVM(int id, String createdDate, Double amount) {
        Id = id;
        CreatedDate = createdDate;
        Amount = amount;
    }
}
