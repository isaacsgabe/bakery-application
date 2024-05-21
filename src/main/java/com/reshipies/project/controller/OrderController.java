package com.reshipies.project.controller;

import com.reshipies.project.DTO.CompleteDataDTO;
import com.reshipies.project.model.*;
import com.reshipies.project.service.EmailService;
import com.reshipies.project.service.OrdersService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private EmailService emailService;

    Map<Long, Item> bakeryItemMap;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);


    public OrderController() {
        this.bakeryItemMap = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        getAllItems();
    }

    public void getAllItems() {

        String sql = "SELECT * FROM item";
        List<Item> items = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id")); // Assuming the ID is of type long in your Item class
            item.setFoodName(rs.getString("food_name"));
            item.setSize(rs.getString("size"));
            item.setPrice(rs.getDouble("price"));
            return item;
        });
        for (Item i : items) {
            StringBuilder sb = new StringBuilder();
            sb.append(i.getFoodName());
            if (i.getSize() != null) {
                sb.append(" ").append(i.getSize());
            }
            this.bakeryItemMap.put(i.getId(), i);
        }
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
        notifyOwner(order, ownerEmail);// Replace with the actual owner's email
        return ResponseEntity.ok("Order successfully added");
    }

    private void notifyOwner(CombinedOrder order, String ownerEmail) {
        String subject = "New Order Placed";
        String htmlString = getHtmlString(order);
        logger.info("sending email now");
        emailService.sendHTMLEmail(ownerEmail, subject, htmlString);
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

    private String getHtmlString(CombinedOrder order) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("    <title>New Order Notification</title>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<table class=\"body-wrap\"\n")
                .append("       style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; width: 100%; background-color: #f6f6f6; margin: 0;\"\n")
                .append("       bgcolor=\"#f6f6f6\">\n")
                .append("    <tbody>\n")
                .append("    <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("        <td style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0;\" valign=\"top\"></td>\n")
                .append("        <td class=\"container\" width=\"600\"\n")
                .append("            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; display: block !important; max-width: 600px !important; clear: both !important; margin: 0 auto;\"\n")
                .append("            valign=\"top\">\n")
                .append("            <div class=\"content\"\n")
                .append("                 style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; max-width: 600px; display: block; margin: 0 auto; padding: 20px;\">\n")
                .append("                <table class=\"main\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n")
                .append("                       style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; border-radius: 3px; background-color: #fff; margin: 0; border: 1px solid #e9e9e9;\"\n")
                .append("                       bgcolor=\"#fff\">\n")
                .append("                    <tbody>\n")
                .append("                    <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                        <td class=\"\"\n")
                .append("                            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 16px; vertical-align: top; color: #fff; font-weight: 500; text-align: center; border-radius: 3px 3px 0 0; background-color: #38414a; margin: 0; padding: 20px;\"\n")
                .append("                            align=\"center\" bgcolor=\"#71b6f9\" valign=\"top\">\n")
                .append("                            <a href=\"#\" style=\"font-size:32px;color:#fff;\">Reshipes</a> <br>\n")
                .append("                            <span style=\"margin-top: 10px;display: block;\">New Order Notification</span>\n")
                .append("                        </td>\n")
                .append("                    </tr>\n")
                .append("                    <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                        <td class=\"content-wrap\"\n")
                .append("                            style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 20px;\"\n")
                .append("                            valign=\"top\">\n")
                .append("                            <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n")
                .append("                                   style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                                <tbody>\n")
                .append("                                <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                                    <td class=\"content-block\"\n")
                .append("                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 0 0 20px;\"\n")
                .append("                                        valign=\"top\">\n")
                .append("                                        Hello <strong>").append(order.getCustomerInfo().getName()).append("</strong>,\n")
                .append("                                    </td>\n")
                .append("                                </tr>\n")
                .append("                                <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                                    <td class=\"content-block\"\n")
                .append("                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 0 0 20px;\"\n")
                .append("                                        valign=\"top\">\n")
                .append("                                        <p>A new order has been successfully placed.</p>\n")
                .append("                                        <p>Order Details:</p>\n")
                .append("                                        <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n")
                .append("                                               style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                                            <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                                                <th>Item</th>\n")
                .append("                                                <th>Quantity</th>\n")
                .append("                                                <th>Price</th>\n")
                .append("                                                <th>Total</th>\n")
                .append("                                            </tr>\n");
        double totalAmount = 0;
        for (OrderItem i : order.getOrderItems()) {
            // Check if the item is present in the bakeryItemMap
            String itemName = bakeryItemMap.get(i.getItemID()).getFoodName();
            double price = bakeryItemMap.get(i.getItemID()).getPrice();
            if (itemName == null) {
                bakeryItemMap.clear();
                getAllItems(); // Assuming this method retrieves items and populates bakeryItemMap
                itemName = bakeryItemMap.get(i.getItemID()).getFoodName();
                price = bakeryItemMap.get(i.getItemID()).getPrice();
            }
            double itemTotal = price * i.getQuantity();
            totalAmount += itemTotal;
            htmlContent.append("                                            <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                    .append("                                                <td>").append(itemName).append("</td>\n")
                    .append("                                                <td>").append(i.getQuantity()).append("</td>\n")
                    .append("                                                <td>").append(price).append("</td>\n")
                    .append("                                                <td>").append(itemTotal).append("</td>\n")
                    .append("                                            </tr>\n");
        }

        htmlContent.append("                                            <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                                                <td colspan=\"3\" style=\"text-align: right;\">Total Amount</td>\n")
                .append("                                                <td>").append(totalAmount).append("</td>\n")
                .append("                                            </tr>\n")
                .append("                                        </table>\n")
                .append("                                    </td>\n")
                .append("                                </tr>\n")
                .append("                                <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                                    <td class=\"content-block\"\n")
                .append("                                        style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0; padding: 0 0 20px;\"\n")
                .append("                                        valign=\"top\">\n")
                .append("                                        Best regards,<br>Reshi Isaacs\n")
                .append("                                    </td>\n")
                .append("                                </tr>\n")
                .append("                                </tbody>\n")
                .append("                            </table>\n")
                .append("                        </td>\n")
                .append("                    </tr>\n")
                .append("                    </tbody>\n")
                .append("                </table>\n")
                .append("                <div class=\"footer\"\n")
                .append("                     style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; width: 100%; clear: both; color: #999; margin: 0; padding: 20px;\">\n")
                .append("                    <table width=\"100%\"\n")
                .append("                           style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                        <tbody>\n")
                .append("                        <tr style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; margin: 0;\">\n")
                .append("                            <td class=\"aligncenter content-block\"\n")
                .append("                                style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 12px; vertical-align: top; color: #999; text-align: center; margin: 0; padding: 0 0 20px;\"\n")
                .append("                                align=\"center\" valign=\"top\"><a href=\"#\"\n")
                .append("                                                               style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 12px; color: #999; text-decoration: underline; margin: 0;\">Unsubscribe</a>\n")
                .append("                                from these alerts.\n")
                .append("                            </td>\n")
                .append("                        </tr>\n")
                .append("                        </tbody>\n")
                .append("                    </table>\n")
                .append("                </div>\n")
                .append("            </div>\n")
                .append("        </td>\n")
                .append("        <td style=\"font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif; box-sizing: border-box; font-size: 14px; vertical-align: top; margin: 0;\" valign=\"top\"></td>\n")
                .append("    </tr>\n")
                .append("    </tbody>\n")
                .append("</table>\n")
                .append("</body>\n")
                .append("</html>");
        return htmlContent.toString();
    }
}
