package dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Vehicle_Model implements Serializable {

    @Expose
    private int id;

    @Expose
    private String name;

    private Vehicle_Brand vehicle_Brand;

    private Vehicle_Model vehicle_model;

    public Vehicle_Model() {
    }

    public Vehicle_Model(int id, String name, Vehicle_Brand vehicle_Brand, Vehicle_Model vehicle_model) {
        this.id = id;
        this.name = name;
        this.vehicle_Brand = vehicle_Brand;
        this.vehicle_model = vehicle_model;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vehicle_Brand getVehicle_Brand() {
        return vehicle_Brand;
    }

    public void setVehicle_Brand(Vehicle_Brand vehicle_Brand) {
        this.vehicle_Brand = vehicle_Brand;
    }

    public Vehicle_Model getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(Vehicle_Model vehicle_model) {
        this.vehicle_model = vehicle_model;
    }
}
