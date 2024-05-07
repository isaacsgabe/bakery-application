package com.reshipies.project.service;

import com.reshipies.project.model.Item;
import com.reshipies.project.repository.CustomerRepository;
import com.reshipies.project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Override
    public Item saveItem(Item item) {
        return this.itemRepository.save(item);
    }
}
