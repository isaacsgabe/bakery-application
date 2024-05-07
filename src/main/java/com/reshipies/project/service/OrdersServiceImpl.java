package com.reshipies.project.service;

import com.reshipies.project.model.CustomerOrder;
import com.reshipies.project.model.OrderItem;
import com.reshipies.project.repository.OrdersRepository;
import com.reshipies.project.repository.OrderItemsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@PersistenceContext
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private EntityManager entityManager;


    @Override
    public String saveOrder(CustomerOrder order) {
        this.ordersRepository.save(order);
        return "saved this order";
    }

    @Override
    public String saveOrderItems(List<OrderItem> items) {
        this.orderItemsRepository.saveAll(items);
        return "saved all items in the order";
    }

    public long getNextOrderId() {
        Query query = entityManager.createQuery("SELECT MAX(customerorder.order_id) FROM CustomerOrder customerorder");
        List resultList = query.getResultList();
        Long maxId = (Long) resultList.get(0); // Assuming there's at least one order item
        if (maxId == null) {
            return 1L; // If no order items exist yet, start from 1
        } else {
            return maxId + 1; // Increment the highest ID
        }
    }



}
