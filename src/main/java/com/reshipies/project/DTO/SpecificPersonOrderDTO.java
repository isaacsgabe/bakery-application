package com.reshipies.project.DTO;

import lombok.Data;

@Data
public class SpecificPersonOrderDTO{
    private String name;
    private String foodName;
    private Long itemId;
    private Integer quantity;
    private Long orderId;
}
