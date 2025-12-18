package dto;

import com.google.gson.annotations.Expose;

public class UserHomeDetailsDTO {
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
    private String joined_datetime;
    @Expose
    private String no;
    @Expose
    private int userId;
    @Expose
    private String street1;
    @Expose
    private String street2;
    @Expose
    private String city; // City name
    @Expose
    private String is_user_paid;
    @Expose
    private String company_id;
    @Expose
    private String is_company_admin;

    public UserHomeDetailsDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getJoined_datetime() {
        return joined_datetime;
    }

    public void setJoined_datetime(String joined_datetime) {
        this.joined_datetime = joined_datetime;
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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getIs_company_admin() {
        return is_company_admin;
    }

    public void setIs_company_admin(String is_company_admin) {
        this.is_company_admin = is_company_admin;
    }

    public String getIs_user_paid() {
        return is_user_paid;
    }

    public void setIs_user_paid(String is_user_paid) {
        this.is_user_paid = is_user_paid;
    }
}