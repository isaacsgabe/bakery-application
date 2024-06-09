package com.reshipies.project.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ThisWeeksOrdersDTO {
    private String customerName;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private String foodName;
    private int quantity;
    private long orderId;
}
