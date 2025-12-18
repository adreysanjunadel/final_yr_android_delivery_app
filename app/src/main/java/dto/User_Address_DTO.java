package dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class User_Address_DTO implements Serializable {

    private User_DTO user_dto;

    @Expose
    private String no;

    @Expose
    private String street1;

    @Expose
    private String street2;

    private City_DTO city;

    public User_Address_DTO() {
    }

    public User_Address_DTO(City_DTO city, String no, String street1, String street2, User_DTO user_dto) {
        this.city = city;
        this.no = no;
        this.street1 = street1;
        this.street2 = street2;
        this.user_dto = user_dto;
    }

    public User_DTO getUser_dto() {
        return user_dto;
    }

    public void setUser_dto(User_DTO user_dto) {
        this.user_dto = user_dto;
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

    public City_DTO getCity() {
        return city;
    }

    public void setCity(City_DTO city) {
        this.city = city;
    }


}
