package com.example.alleoindong.cabtap.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/26/16.
 */

@IgnoreExtraProperties
public class Booking {
    public String id;
    public String plateNumber;
    public String status;
    public double fareEstimate;
    public LatLng passengerLocation;
    public LatLng destination;

    public Booking() {

    }

    public Booking(String id, String status, double fareEstimate,
                   LatLng passengerLocation, LatLng destination) {

        this.id = id;
        this.status = status;
        this.fareEstimate = fareEstimate;
        this.passengerLocation = passengerLocation;
        this.destination = destination;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
