package dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class City_DTO implements Serializable {

    @Expose
    private int id;

    @Expose
    private String name;

    private Province_DTO province;

    public City_DTO() {
    }

    public City_DTO(int id, String name, Province_DTO province) {
        this.id = id;
        this.name = name;
        this.province = province;
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

    public Province_DTO getProvince() {
        return province;
    }

    public void setProvince(Province_DTO province) {
        this.province = province;
    }
}
