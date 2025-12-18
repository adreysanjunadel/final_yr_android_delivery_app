package dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class Driver_DTO implements Serializable {

    @Expose
    private int id;

    @Expose
    private String fname;

    @Expose
    private String lname;

    @Expose
    private String email;

    @Expose
    private String nic;

    @Expose
    private String mobile;

    @Expose String date;

    @Expose
    private String joined_datetime;


    public Driver_DTO(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJoined_datetime() {
        return joined_datetime;
    }

    public void setJoined_datetime(String joined_datetime) {
        this.joined_datetime = joined_datetime;
    }
}
