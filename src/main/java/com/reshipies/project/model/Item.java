package com.reshipies.project.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Item{

    @Id
    private Long id;
    private Double price;
    private String size;
    private String foodName;

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", price=" + price +
                ", size='" + size + '\'' +
                ", foodName='" + foodName + '\'' +
                '}';
    }
}
