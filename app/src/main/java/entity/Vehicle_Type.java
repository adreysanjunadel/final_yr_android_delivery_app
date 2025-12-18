package entity;

import java.io.Serializable;

public class Vehicle_Type implements Serializable {

    private Integer id;

    private String name;

    public Vehicle_Type() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
