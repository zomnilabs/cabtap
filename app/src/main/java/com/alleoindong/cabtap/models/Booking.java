package com.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/26/16.
 */

@IgnoreExtraProperties
public class Booking {
    public String id;
    public String passengerId;
    public String plateNumber;
    public String status;
    public String passengerLocationName;
    public String destinationName;
    public double fareEstimate;
    public Location passengerLocation;
    public Location destination;

    public Booking() {

    }

    public Booking(String id, String status, double fareEstimate,
                   Location passengerLocation, Location destination) {

        this.id = id;
        this.status = status;
        this.fareEstimate = fareEstimate;
        this.passengerLocation = passengerLocation;
        this.destination = destination;
    }

    public Booking(String id, String passengerId, String status, double fareEstimate,
                   String passengerLocationName, String destinationName,
                   Location passengerLocation, Location destination) {

        this.id = id;
        this.passengerId = passengerId;
        this.status = status;
        this.fareEstimate = fareEstimate;
        this.passengerLocationName = passengerLocationName;
        this.destinationName = destinationName;
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
