package com.example.alleoindong.cabtap.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by alleoindong on 11/26/16.
 */

public class Booking {
    public String id;
    public String plateNumber;
    public boolean status;
    public double fareEstimate;
    public double actualFare;
    public LatLng passengerLocation;
    public LatLng destination;
}
