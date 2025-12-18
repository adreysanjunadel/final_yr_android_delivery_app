package dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Province_DTO implements Serializable {

    @Expose
    private int id;

    @Expose
    private String name;

    public Province_DTO() {
    }

    public Province_DTO(int id, String name) {
        this.id = id;
        this.name = name;
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
}
