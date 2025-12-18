package dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class CompanyDetailsDTO implements Serializable {
    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private String industry;

    @Expose
    private String is_company_paid;

    @Expose
    private String is_company_approved;

    @Expose
    private String no;
    @Expose
    private String street1;
    @Expose
    private String street2;
    @Expose
    private String city; // City name

    public CompanyDetailsDTO() {
    }

    public CompanyDetailsDTO(String city, String name, int id, String industry, String is_company_approved, String is_company_paid, String no, String street1, String street2) {
        this.city = city;
        this.name = name;
        this.id = id;
        this.industry = industry;
        this.is_company_approved = is_company_approved;
        this.is_company_paid = is_company_paid;
        this.no = no;
        this.street1 = street1;
        this.street2 = street2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getIs_company_approved() {
        return is_company_approved;
    }

    public void setIs_company_approved(String is_company_approved) {
        this.is_company_approved = is_company_approved;
    }

    public String getIs_company_paid() {
        return is_company_paid;
    }

    public void setIs_company_paid(String is_company_paid) {
        this.is_company_paid = is_company_paid;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
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
}
