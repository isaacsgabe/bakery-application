package com.reshipies.project.service;

import com.reshipies.project.model.Customer;
import com.reshipies.project.model.CustomerOrder;
import com.reshipies.project.model.OrderItem;
import com.reshipies.project.repository.CustomerRepository;
import com.reshipies.project.repository.OrdersRepository;
import com.reshipies.project.repository.OrderItemsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Long saveCustomer(Customer customer) {
        try {
            logger.info("Saving customer: {}", customer);
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Customer saved with ID: {}", savedCustomer.getId());
            return savedCustomer.getId();
        } catch (Exception e) {
            logger.error("Error occurred while saving customer: {}", customer);
            logger.error("Stack trace: ", e);
            throw e; // Re-throw the exception to propagate it
        }
    }

    @Override
    public String saveOrder(CustomerOrder order) {
        try {
            logger.info("Saving order: {}", order);
            this.ordersRepository.save(order);
            logger.info("Order saved");
            return "saved this order";
        } catch (Exception e) {
            logger.error("Error occurred while saving order: {}", order);
            logger.error("Stack trace: ", e);
            throw e; // Re-throw the exception to propagate it
        }
    }

    @Override
    public String saveOrderItems(List<OrderItem> items) {
        try {
            logger.info("Saving order items: {}", items);
            this.orderItemsRepository.saveAll(items);
            logger.info("Order items saved");
            return "saved all items in the order";
        } catch (Exception e) {
            logger.error("Error occurred while saving order items: {}", e.getMessage());
            logger.error("Stack trace: ", e);
            throw e; // Re-throw the exception to propagate it
        }
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
