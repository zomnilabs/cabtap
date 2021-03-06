package com.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/19/16.
 */

@IgnoreExtraProperties
public class UserProfile {
    public String id;
    public String uid;
    public String firstName;
    public String lastName;
    public String address;
    public String contactNumber;
    public String dateBirth;
    public String role;
    public String email;
    public String fcmToken;

    public UserProfile() {

    }

    public UserProfile(String id, String uid, String firstName, String lastName,
                       String address, String contactNumber, String dateBirth, String role, String email) {
        this.id = id;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dateBirth = dateBirth;
        this.role = role;
        this.email = email;
    }

    public UserProfile(String id, String uid, String firstName, String lastName, String role, String email) {
        this.id = id;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
    }

    public UserProfile(String id, String uid, String firstName,
                       String lastName, String role, String email, String fcmToken) {
        this.id = id;
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.fcmToken = fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
