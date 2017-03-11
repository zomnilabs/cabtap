package com.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/30/16.
 */

@IgnoreExtraProperties
public class BookingRequest {
    public String id;
    public String uid;
    public String pickup;
    public String destination;
    private String status;
    private Booking booking;

    public BookingRequest() {

    }

    public BookingRequest(String id, String uid, String status, Booking booking) {
        this.id = id;
        this.uid = uid;
        this.status = status;
        this.booking = booking;
    }

    public BookingRequest(String id, String uid, String status) {
        this.id = id;
        this.uid = uid;
        this.status = status;
    }

    public BookingRequest(String id, String uid, String pickup,
                          String destination, String status, Booking booking) {
        this.id = id;
        this.uid = uid;
        this.pickup = pickup;
        this.destination = destination;
        this.status = status;
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
