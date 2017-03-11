package com.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/19/16.
 */

@IgnoreExtraProperties
public class User {
    public String uid;
    public String email;
    public String password;
    public UserProfile userProfile;

    public User() {

    }

    public User(String uid, String email, String password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    public User(String uid, String email, UserProfile userProfile) {
        this.uid = uid;
        this.email = email;
        this.userProfile = userProfile;
    }
}
