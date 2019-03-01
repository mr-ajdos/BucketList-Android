package com.example.ajdin.wishlist.helpers;

import java.io.Serializable;

/**
 * Created by Student on 09.12.2016..
 */
public interface MyRunnable<T> extends Serializable
{
    void  run(T t);
}

