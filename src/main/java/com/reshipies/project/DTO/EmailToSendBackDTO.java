package com.reshipies.project.DTO;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class EmailToSendBackDTO {

    private double totalAmount;
    private List<IndividualItem> allItems;
    private String name;
}
