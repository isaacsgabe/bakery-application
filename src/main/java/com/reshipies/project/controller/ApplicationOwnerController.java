package com.reshipies.project.controller;

import com.reshipies.project.DTO.*;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reshipes")
public class ApplicationOwnerController{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/thisWeeksOrders")
    public Map<String, Object> getThisWeeksOrders() {
        String sql = "SELECT c.name, co.order_date, co.order_time,co.order_id," +
                " item.food_name, oi.quantity FROM customer AS c INNER JOIN " +
                "customer_order AS co ON c.id = co.customer_id INNER JOIN order_item AS " +
                "oi ON co.order_id = oi.order_id INNER JOIN item ON item.id = oi.item_id " +
                "WHERE co.order_date >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY((CURDATE())+2) DAY);\n";

        List<ThisWeeksOrdersDTO> orders = jdbcTemplate.query(sql, (rs, rowNum) -> {
            ThisWeeksOrdersDTO dto = new ThisWeeksOrdersDTO();
            dto.setCustomerName(rs.getString("name"));
            dto.setOrderDate(rs.getDate("order_date").toLocalDate());
            dto.setOrderTime(rs.getTime("order_time").toLocalTime());
            dto.setFoodName(rs.getString("food_name"));
            dto.setQuantity(rs.getInt("quantity"));
            dto.setOrderId(rs.getLong("order_id"));
            return dto;
        });

        String totalSql = "SELECT SUM(item.price * oi.quantity) AS total_amount FROM customer_order AS co " +
                "INNER JOIN order_item AS oi ON co.order_id = oi.order_id " +
                "INNER JOIN item ON item.id = oi.item_id " +
                "WHERE co.order_date >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY((CURDATE())+2) DAY);\n";

        Double totalAmount = jdbcTemplate.queryForObject(totalSql, Double.class);
        try{
            totalAmount = (double) Math.round(totalAmount);
        }catch (NullPointerException e){
            System.out.println("this should be be positive");
            totalAmount = 0.0;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("totalAmount", totalAmount);
        return response;
    }


    @GetMapping("/thisWeeksTotals")
    public List<FoodQuantityDTO> getThisWeeksTotals() {
        String sql = "SELECT item.food_name, SUM(oi.quantity) AS total_quantity " +
                "FROM customer AS c INNER JOIN customer_order AS co ON c.id = co.customer_id " +
                "INNER JOIN order_item AS oi ON co.order_id = oi.order_id INNER JOIN item " +
                "ON item.id = oi.item_id WHERE co.order_date >= DATE_SUB(CURDATE(), " +
                "INTERVAL WEEKDAY((CURDATE())+2) DAY) GROUP BY item.food_name order by food_name;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String foodName = rs.getString("food_name");
            int totalQuantity = rs.getInt("total_quantity");
            return new FoodQuantityDTO(foodName, totalQuantity);
        });
    }

    @GetMapping("/getCustomerOrder")
    public List<SpecificPersonOrderDTO> getOrderDetailsByCustomerName(@RequestParam String customerName) {
        String sql = "SELECT name, item.food_name, oi.item_id, oi.quantity, co.order_id " +
                "FROM customer AS c " +
                "INNER JOIN customer_order AS co ON c.id = co.customer_id " +
                "INNER JOIN order_item AS oi ON co.order_id = oi.order_id " +
                "INNER JOIN item ON item.id = oi.item_id " +
                "WHERE co.order_date >= DATE_SUB(CURDATE(), INTERVAL WEEKDAY((CURDATE())+2) DAY) " +
                "AND name = \"" + customerName + "\";";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SpecificPersonOrderDTO specificPersonOrderDTO = new SpecificPersonOrderDTO();
            specificPersonOrderDTO.setName(rs.getString("name"));
            specificPersonOrderDTO.setFoodName(rs.getString("food_name"));
            specificPersonOrderDTO.setItemId(rs.getLong("item_id"));
            specificPersonOrderDTO.setQuantity(rs.getInt("quantity"));
            specificPersonOrderDTO.setOrderId(rs.getLong("order_id"));
            return specificPersonOrderDTO;
        });
    }

    @GetMapping("/thisWeeksCities")
    public List<thisWeeksCitiesDTO> getThisWeeksCities() {
        String sql = "SELECT c.name AS customerName, c.street_address, c.city, c.state, c.zip_code, c.apt_number " +
                "FROM customer AS c " +
                "INNER JOIN customer_order AS co ON c.id = co.customer_id " +
                "WHERE co.order_date >= DATE_SUB(CURDATE(), WEEKDAY((CURDATE())+2) DAY) " +
                "ORDER BY c.city;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            thisWeeksCitiesDTO dto = new thisWeeksCitiesDTO();
            dto.setCustomerName(rs.getString("customerName"));
            dto.setStreetAddress(rs.getString("street_address"));
            dto.setCity(rs.getString("city"));
            dto.setState(rs.getString("state"));
            dto.setZipCode(rs.getString("zip_code"));
            dto.setAptNumber(rs.getString("apt_number"));

            return dto;
        });
    }




    @GetMapping("/data")
    public List<CompleteDataDTO> getData() {
        String sql = "SELECT name, street_address,apt_number,city, state, zip_code, email, phone_number, " +
                "oi.order_id, customer_id, order_date, order_time, order_item_id, food_name, quantity" +
                " FROM customer AS c inner JOIN customer_order AS co ON c.id = co.customer_id " +
                "INNER JOIN order_item AS oi ON co.order_id = oi.order_id INNER JOIN item " +
                "ON item.id = oi.item_id;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CompleteDataDTO dto = new CompleteDataDTO();
            dto.setName(rs.getString("name"));
            dto.setStreetAddress(rs.getString("street_address"));
            dto.setAptNumber(rs.getString("apt_number"));
            dto.setCity(rs.getString("city"));
            dto.setState(rs.getString("state"));
            dto.setZipCode(rs.getString("zip_code"));
            dto.setEmail(rs.getString("email"));
            dto.setPhoneNumber(rs.getString("phone_number"));
            dto.setOrderId(rs.getInt("order_id"));
            dto.setCustomerId(rs.getInt("customer_id"));
            dto.setOrderDate(rs.getDate("order_date"));
            dto.setOrderTime(rs.getTime("order_time"));
            dto.setOrderItemId(rs.getInt("order_item_id"));
            dto.setFoodName(rs.getString("food_name"));
            dto.setQuantity(rs.getInt("quantity"));
            return dto;
        });
    }
}
