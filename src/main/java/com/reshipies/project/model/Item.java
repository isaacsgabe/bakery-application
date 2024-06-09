package com.reshipies.project.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Objects;

@Entity
@Data
public class Item{

    @Id
    private Long id;
    private Double price;
    private String size;
    private String foodName;
    private String category;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", price=" + price +
                ", size='" + size + '\'' +
                ", foodName='" + foodName + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!Objects.equals(price, item.price)) return false;
        if (!Objects.equals(size, item.size)) return false;
        if (!Objects.equals(foodName, item.foodName)) return false;
        return Objects.equals(category, item.category);
    }

}
