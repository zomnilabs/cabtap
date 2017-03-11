package com.alleoindong.cabtap.models;

/**
 * Created by alleoindong on 11/25/16.
 */

public class Maintenance {
    public String id;
    public String maintenance;
    public double cost;
    public String maintenanceDate;
    public String plateNumber;

    public Maintenance() {

    }

    public Maintenance(String id, String maintenance,
                       double cost, String maintenanceDate, String plateNumber) {
        this.id = id;
        this.maintenance = maintenance;
        this.cost = cost;
        this.maintenanceDate = maintenanceDate;
        this.plateNumber = plateNumber;
    }
}
