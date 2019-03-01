package com.example.ajdin.wishlist.viewModels;

import java.io.Serializable;
import java.util.List;

public class WishDetailsVM implements Serializable {
    public int Id;
    public String Name;
    public Double Amount;
    public int UserId;
    public Double AmountSaved;
    public Double Difference;
    public Double Percentage;
}
