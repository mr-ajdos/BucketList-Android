package com.example.ajdin.wishlist.helpers;

import android.app.Activity;


public class Util {

    public static void otvoriFragmentKaoReplace(android.support.v4.app.FragmentActivity  activity, int id, android.support.v4.app.Fragment fragment)
    {
        final android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    public static void otvoriFragmentKaoReplace(Activity activity, int id, android.app.Fragment fragment)
    {
        final android.app.FragmentManager fm = activity.getFragmentManager();
        final android.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(id, fragment);
        fragmentTransaction.commit();
    }

    public static void otvoriFragmentKaoDijalog(android.support.v4.app.FragmentActivity activity, android.support.v4.app.DialogFragment fragment) {
        final android.support.v4.app.FragmentManager fm = activity.getSupportFragmentManager();
        fragment.show(fm, "tag");
    }
    public static void otvoriFragmentKaoDijalog(Activity activity, android.app.DialogFragment fragment) {
        final android.app.FragmentManager fm = activity.getFragmentManager();
        fragment.show(fm, "tag");
    }
}
