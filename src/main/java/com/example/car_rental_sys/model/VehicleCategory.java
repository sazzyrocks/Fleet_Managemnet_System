package com.example.car_rental_sys.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Category classification for fleet vehicles (Sedan, SUV, Luxury, Electric, etc.).
 */
public class VehicleCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer categoryId;
    private String name;
    private String description;

    public VehicleCategory() {
    }

    public VehicleCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public VehicleCategory(Integer categoryId, String name, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleCategory that)) return false;
        return Objects.equals(categoryId, that.categoryId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, name);
    }

    @Override
    public String toString() {
        return name != null ? name : "Category#" + categoryId;
    }
}
