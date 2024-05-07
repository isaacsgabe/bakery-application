package com.reshipies.project.controller;

import com.reshipies.project.model.Customer;
import com.reshipies.project.model.Item;
import com.reshipies.project.service.CustomerService;
import com.reshipies.project.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/add")
    public String add(@RequestBody Item item){
        itemService.saveItem(item);
        return "new item is added";
    }
}
