package com.example.alleoindong.cabtap.models;

/**
 * Created by alleoindong on 11/25/16.
 */

public class Maintenance {
    public String id;
    public String vehicle_id;
    public String maintenance;
    public String cost;
    public String maintenanceDate;
    public String plateNumber;

    public Maintenance() {

    }

    public Maintenance(String id, String vehicle_id, String maintenance,
                       String cost, String maintenanceDate) {
        this.id = id;
        this.vehicle_id = vehicle_id;
        this.maintenance = maintenance;
        this.cost = cost;
        this.maintenanceDate = maintenanceDate;
    }
}
