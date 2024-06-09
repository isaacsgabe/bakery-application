package com.reshipies.project.DTO;

import lombok.Data;

@Data
public class FoodQuantityDTO {
    private String foodName;
    private int totalQuantity;

    public FoodQuantityDTO(String foodName, int totalQuantity) {
        this.foodName = foodName;
        this.totalQuantity = totalQuantity;
    }

    // Constructors can be omitted with Lombok
}
