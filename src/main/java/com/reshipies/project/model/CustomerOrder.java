package com.reshipies.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class CustomerOrder {
    @Id
    private Long order_id;
    private Long customerId;
    private LocalDate orderDate;
    private LocalTime orderTime;
}
