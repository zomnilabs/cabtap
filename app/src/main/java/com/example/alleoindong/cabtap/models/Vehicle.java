package com.example.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/21/16.
 */

@IgnoreExtraProperties
public class Vehicle {
    public String plateNumber;
    public String make;
    public String model;
    public String year;
    public String uid;

    public Vehicle() {

    }

    public Vehicle(String plateNumber, String make, String model, String year, String uid) {
        this.plateNumber = plateNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.uid = uid;
    }
}
