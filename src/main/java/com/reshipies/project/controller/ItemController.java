package com.reshipies.project.controller;

import com.reshipies.project.model.Item;
import com.reshipies.project.model.OrderItem;
import com.reshipies.project.service.ItemService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderController oc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Set<Item> itemsToHide;

    private Logger logger = LoggerFactory.getLogger(ItemController.class);

    public ItemController(){
        this.itemsToHide = new HashSet<>();
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody Item item) {
        logger.info("Someone tried adding an item: {}", item);
        if(itemsToHide.contains(item)){
            this.itemsToHide.remove(item);
            return ResponseEntity.ok("New item is added");
        }
        try {
            itemService.saveItem(item);
            logger.info("add method for item was successful");
            return ResponseEntity.ok("New item is added");
        } catch (DataIntegrityViolationException e) {
            logger.error("Error adding item: {}", e.getMessage());
            logger.info("Inputted a duplicate");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Item already exists");
        } catch (Exception e) {
            logger.error("Error adding item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while adding item");
        }
    }

    @PutMapping("/updatePrice")
    public ResponseEntity<String> updatePrice(@RequestParam Long itemId, @RequestParam double newPrice) {
        logger.info("Someone tried updating the price of item with ID: {} to {}", itemId, newPrice);
        try {
            itemService.updateItemPrice(itemId, newPrice);
            logger.info("Update price method for item was successful");
            return ResponseEntity.ok("Item price is updated");
        } catch (NoSuchElementException e) {
            logger.error("Error updating item price: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        } catch (Exception e) {
            logger.error("Error updating item price: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating item price");
        }
    }

    @DeleteMapping("/removeItem")
    public ResponseEntity<String> removeItem(@RequestParam Long itemId) {
        try {
            // Define the SQL query
            String sql = "DELETE FROM item WHERE id = ?";

            // Execute the DELETE query
            int rowsAffected = jdbcTemplate.update(sql, itemId);

            // Check if any rows were affected
            if (rowsAffected > 0) {
                return ResponseEntity.ok("Item removed successfully");
            } else {
                // No item found to delete
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
            }
        } catch (Exception e) {
            // Handle exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting item");
        }
    }

    @GetMapping("/getAllItem")
    public List<Item> getAllItems() {
        String sql = "SELECT * FROM item";

        // Log the SQL query
        logger.info("Executing SQL query: " + sql);

        List<Item> items = (List<Item>) oc.getAllItemsMap().values();

        // Log the items after filtering
        logger.info("Items after filtering: " + items);

        return items;
    }

    @PostMapping("/addItems")
    @Transactional
    public ResponseEntity<String> add(@RequestBody List<Item> items) {
        logger.info("add method for multiple items was called: {}", items);
        try {
            for (Item item : items) {
                if(!itemsToHide.contains(item)){
                    itemService.saveItem(item);
                }else{
                    itemsToHide.remove(item);
                }

            }
            logger.info("All items added successfully");
            return ResponseEntity.ok("All items added successfully");
        } catch (DataIntegrityViolationException e) {
            logger.error("Error adding item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Item already exists");
        } catch (IllegalArgumentException e) {
            logger.error("Error adding item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid item data");
        } catch (Exception e) {
            logger.error("Error adding items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while adding items");
        }
    }
}
