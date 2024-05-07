package com.reshipies.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Customer {
    public Customer() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Name;
    private String email;
    private String phoneNumber;
    private String address;

}
