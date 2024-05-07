package com.reshipies.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "order_item") // Specify the table name if it differs from the entity name
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id") // Specify the column name if it differs from the field name
    private Long orderItemID;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "order_id")
    private long orderID;

    @Column(name = "item_id")
    private int itemID;

}


