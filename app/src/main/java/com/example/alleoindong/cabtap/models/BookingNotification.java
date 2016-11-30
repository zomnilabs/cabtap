package com.example.alleoindong.cabtap.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by alleoindong on 11/30/16.
 */

@IgnoreExtraProperties
public class BookingNotification {
    public String id;
    public String uid;
    private String status;
    private Booking booking;

    public BookingNotification() {

    }

    public BookingNotification(String id, String uid, String status, Booking booking) {
        this.id = id;
        this.uid = uid;
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
