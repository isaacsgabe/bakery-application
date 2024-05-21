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
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String aptNumber;

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + Name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", aptNumber='" + aptNumber + '\'' +
                '}';
    }

}
