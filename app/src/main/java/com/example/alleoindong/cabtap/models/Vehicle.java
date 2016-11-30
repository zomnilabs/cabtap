package com.example.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) return true;

        if (!(obj instanceof Vehicle)) {
            return false;
        }

        Vehicle vehicle = (Vehicle) obj;

        return this.plateNumber.equals(vehicle.plateNumber);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + plateNumber.hashCode();

        return result;
    }
}
