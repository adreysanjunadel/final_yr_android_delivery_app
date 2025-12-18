package dto;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import entity.Category;

public class Item_Brand implements Serializable {

    @Expose
    private int id;

    @Expose
    private String name;

    private Category category;

    public Item_Brand() {
    }

    public Item_Brand(Category category, int id, String name) {
        this.category = category;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
