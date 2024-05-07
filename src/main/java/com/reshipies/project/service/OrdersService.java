package com.reshipies.project.service;

import com.reshipies.project.model.CustomerOrder;
import com.reshipies.project.model.OrderItem;

import java.util.List;

public interface OrdersService {
    public String saveOrder(CustomerOrder order);

    public String saveOrderItems(List<OrderItem> items);

    public long getNextOrderId();
}
