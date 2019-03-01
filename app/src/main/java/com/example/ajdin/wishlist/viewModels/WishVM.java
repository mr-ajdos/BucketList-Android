package com.example.ajdin.wishlist.viewModels;

import java.io.Serializable;

public class WishVM implements Serializable {

    public int Id;
    public String Name;
    public Double Amount;
    public Double AmountSaved;

    public WishVM(int id, String name, Double amount, Double amountSaved) {
        Id = id;
        Name = name;
        Amount = amount;
        AmountSaved = amountSaved;
    }
}
