package com.reshipies.project.DTO;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

@Data
public class CompleteDataDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String streetAddress;
    private String aptNumber;
    private String city;
    private String state;
    private String zipCode;
    private int orderId;
    private int customerId;
    private Date orderDate;
    private Time orderTime;
    private int orderItemId;
    private String foodName;
    private int quantity;
}
