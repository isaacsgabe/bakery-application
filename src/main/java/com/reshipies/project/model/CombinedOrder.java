package com.reshipies.project.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class CombinedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @OneToOne(cascade = CascadeType.ALL) // Ensure cascading operations
    private CustomerOrder customerOrder;

    @OneToMany(cascade = CascadeType.ALL) // Ensure cascading operations
    private List<OrderItem> orderItems;
}
