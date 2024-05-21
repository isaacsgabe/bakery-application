package com.reshipies.project.controller;

import com.reshipies.project.model.Item;
import com.reshipies.project.service.ItemService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Set<Long> itemsToHide;

    private Logger logger = LoggerFactory.getLogger(ItemController.class);

    public ItemController(){
        this.itemsToHide = new HashSet<>();
    }

    @PostMapping("/add")
    public String add(@RequestBody Item item){
        logger.info("Someone tried adding an item: {}",item);
        try{
            itemService.saveItem(item);
            logger.info("add method for item was successful");
            return "new item is added";
        }catch (Exception e){
            logger.info("add method for item was not successful because of the following error: {}",e.getMessage());
            return "item not succussfully added";
        }
    }

    @PutMapping("/updatePrice")
    public String updatePrice(@RequestParam Long itemId, @RequestParam double newPrice) {
        logger.info("Someone tried updating the price of item with ID: {} to {}", itemId, newPrice);
        try {
            itemService.updateItemPrice(itemId, newPrice);
            logger.info("Update price method for item was successful");
            return "Item price is updated";
        } catch (Exception e) {
            logger.info("Update price method for item was not successful because of the following error: {}", e.getMessage());
            return "Item price not successfully updated";
        }
    }

    @DeleteMapping("/removeItem")
    public void removeItem(@RequestParam Long itemId) {
        this.itemsToHide.add(itemId);
    }

    @GetMapping("/getAllItem")
    public List<Item> getAllItems() {
        String sql = "SELECT * FROM item";
        List<Item> items = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id")); // Assuming the ID is of type long in your Item class
            item.setFoodName(rs.getString("food_name"));
            item.setSize(rs.getString("size"));
            item.setPrice(rs.getDouble("price"));
            return item;
        });
        items.removeIf(item -> this.itemsToHide.contains(item.getId()));
        return items;
    }
    @PostMapping("/addItems")
    @Transactional
    public String add(@RequestBody List<Item> items){
        logger.info("add method for multiple items was called: {}", items);
            for(Item item:items){
                try{
                    itemService.saveItem(item);
                }catch (Exception e){
                    logger.info("add method for item was not successful because of the following error: {}",e.getMessage());
                    return "item not succussfully added";
                }
            }
            return "new item is added";
    }
}
