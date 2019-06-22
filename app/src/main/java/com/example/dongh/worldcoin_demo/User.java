package com.example.dongh.worldcoin_demo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String email;
    public int coin;
    public String code;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, int coin , String code) {
        this.email = email;
        this.coin = coin;
        this.code = code;
    }

}
