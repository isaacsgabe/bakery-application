package com.reshipies.project.controller;

import com.reshipies.project.model.*;
import com.reshipies.project.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    public OrdersService ordersService;


    @PostMapping("/add")
    public String add(@RequestBody CombinedOrder order){
        String toReturn = "";
        long id = ordersService.getNextOrderId();
        CustomerOrder customerOrder = order.getCustomerOrder();
        customerOrder.setOrder_id(id);
        customerOrder.setOrderDate(LocalDate.now());
        customerOrder.setOrderTime(LocalTime.now());
        ordersService.saveOrder(customerOrder);
        toReturn += "added Order Info";
        for(OrderItem i: order.getOrderItems() ){
            i.setOrderID(id);
        }
        ordersService.saveOrderItems(order.getOrderItems());
        return toReturn + " added the items as well";
    }



}
