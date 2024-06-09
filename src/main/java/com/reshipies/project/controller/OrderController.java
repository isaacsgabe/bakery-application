package com.reshipies.project.controller;


import com.reshipies.project.model.*;
import com.reshipies.project.service.EmailService;
import com.reshipies.project.service.OrdersService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private EmailService emailService;

    private Map<Long, Item> bakeryItemMap;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(){
        bakeryItemMap = new HashMap<>();
    }

    public Map<Long, Item> getAllItemsMap() {
        if(bakeryItemMap == null){
            bakeryItemMap = new HashMap<>();
        }
        this.bakeryItemMap.clear();
        String sql = "SELECT * FROM item";
        logger.info("Executing SQL query: " + sql);
        List<Item> items = getItemsQuery(sql, jdbcTemplate);
        for (Item i : items) {
            StringBuilder sb = new StringBuilder();
            sb.append(i.getFoodName());
            if (i.getSize() != null) {
                sb.append(" ").append(i.getSize());
            }
            bakeryItemMap.put(i.getId(), i);
        }
        return bakeryItemMap;
    }

    private List<Item> getItemsQuery(String sql, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id")); // Assuming the ID is of type long in your Item class
            item.setFoodName(rs.getString("food_name"));
            item.setSize(rs.getString("size"));
            item.setPrice(rs.getDouble("price"));
            item.setCategory(rs.getString("category"));
            return item;
        });
    }

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<String> add(@RequestBody CombinedOrder order) {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty() ||
                order.getCustomerInfo() == null) {
            throw new IllegalArgumentException("either the customer info is wrong or there were no items ordered");
        }
        String ownerEmail = order.getCustomerInfo().getEmail();
        // Log that the add method has been called
        logger.info("Someone placed and order: {}", order);
        // Save customer information
        Long customerId = saveCustomer(order.getCustomerInfo());
        // Save order information
        long orderId = saveCustomerOrder(order.getCustomerOrder(), customerId);
        // Save order items
        saveOrderItems(order.getOrderItems(), orderId);
        logger.info("Order successfully added");
        // Notify the owner
        notifyOwner(order, ownerEmail,bakeryItemMap);// Replace with the actual owner's email

        //notify Bakery Owner
        notifyOwner(order, "reshipesbakery@gmail.com",bakeryItemMap);// Replace with the actual owner's email

        return ResponseEntity.ok("Order successfully added");
    }

    private void notifyOwner(CombinedOrder order, String ownerEmail, Map<Long, Item> bakeryItemMap) {
        String subject = "New Order Placed";
        logger.info("sending email now");
        emailService.sendHTMLEmail(ownerEmail, subject, order, bakeryItemMap);
    }


    private Long saveCustomer(Customer customer) {
        try {
            Long customerId = ordersService.saveCustomer(customer);
            logger.debug("Customer information saved with ID: {}", customerId);
            return customerId;
        } catch (Exception e) {
            // Log any exceptions that occur
            logger.error("Error occurred while adding order: {}", e.getMessage());
            // You can also log the stack trace if needed
            logger.error("Stack trace: ", e);
            // Return an appropriate error message
            return (long) -1;
        }
    }

    private long saveCustomerOrder(CustomerOrder customerOrder, Long customerId) {
        try {
            long orderId = ordersService.getNextOrderId();
            customerOrder.setOrder_id(orderId);
            customerOrder.setCustomerId(customerId);
            customerOrder.setOrderDate(LocalDate.now());
            customerOrder.setOrderTime(LocalTime.now());
            ordersService.saveOrder(customerOrder);
            logger.debug("Order information saved with ID: {}", orderId);
            return orderId;
        } catch (Exception e) {
            logger.error("Error occurred while adding order: {}", e.getMessage());
            // You can also log the stack trace if needed
            logger.error("Stack trace: ", e);
            // Return an appropriate error message
            return -1;
        }
    }

    private void saveOrderItems(List<OrderItem> orderItems, long orderId) {
        try {
            for (OrderItem item : orderItems) {
                item.setOrderID(orderId);
            }
            ordersService.saveOrderItems(orderItems);
        } catch (Exception e) {
            logger.error("Error occurred while adding order: {}", e.getMessage());
            // You can also log the stack trace if needed
            logger.error("Stack trace: ", e);
            // Return an appropriate error message
        }
    }
}