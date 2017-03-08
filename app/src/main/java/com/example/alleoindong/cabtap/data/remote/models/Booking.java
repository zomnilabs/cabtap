package com.example.alleoindong.cabtap.data.remote.models;

import com.google.gson.annotations.SerializedName;

public class Booking {
    private Integer id;

    @SerializedName("user_id")
    private Integer userId;

    @SerializedName("vehicle_user_id")
    private Integer vehicleUserId;

    private String pickup;

    private String destination;

    @SerializedName("pickup_lat")
    private Double pickupLat;

    @SerializedName("pickup_lng")
    private Double pickupLng;

    @SerializedName("destination_lat")
    private Double destinationLat;

    @SerializedName("destination_lng")
    private Double destinationLng;

    private String status;

    private Double price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getVehicleUserId() {
        return vehicleUserId;
    }

    public void setVehicleUserId(Integer vehicleUserId) {
        this.vehicleUserId = vehicleUserId;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public Double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public Double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(Double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
