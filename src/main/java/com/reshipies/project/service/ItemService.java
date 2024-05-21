package com.reshipies.project.service;

import com.reshipies.project.model.Customer;
import com.reshipies.project.model.Item;

public interface ItemService {

    public Item saveItem(Item item);
    public long getNextItemId();

    void updateItemPrice(Long itemId, double newPrice);
}
