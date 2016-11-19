package com.example.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/19/16.
 */

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String password;

    public User() {

    }

    public User(String uid, String email, String password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }
}
