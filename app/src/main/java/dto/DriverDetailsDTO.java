package dto;

import com.google.gson.annotations.Expose;

public class DriverDetailsDTO {
    @Expose
    private String id;
    @Expose
    private String fname;
    @Expose
    private String lname;
    @Expose
    private String mobile;
    @Expose
    private String nic;
    @Expose
    private String email;
    @Expose
    private String dob;
    @Expose
    private String joined_datetime;
    @Expose
    private String driver_id;
    @Expose
    private String driver_plate;
    @Expose
    private String title;
    @Expose
    private String vehicle_model_name;
    @Expose
    private String vehicle_brand_name;
    @Expose
    private String vehicle_type_name;

    public DriverDetailsDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getJoined_datetime() {
        return joined_datetime;
    }

    public void setJoined_datetime(String joined_datetime) {
        this.joined_datetime = joined_datetime;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_plate() {
        return driver_plate;
    }

    public void setDriver_plate(String driver_plate) {
        this.driver_plate = driver_plate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVehicle_model_name() {
        return vehicle_model_name;
    }

    public void setVehicle_model_name(String vehicle_model_name) {
        this.vehicle_model_name = vehicle_model_name;
    }

    public String getVehicle_brand_name() {
        return vehicle_brand_name;
    }

    public void setVehicle_brand_name(String vehicle_brand_name) {
        this.vehicle_brand_name = vehicle_brand_name;
    }

    public String getVehicle_type_name() {
        return vehicle_type_name;
    }

    public void setVehicle_type_name(String vehicle_type_name) {
        this.vehicle_type_name = vehicle_type_name;
    }


}
