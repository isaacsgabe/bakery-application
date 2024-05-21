package com.reshipies.project.service;

import com.reshipies.project.model.Item;
import com.reshipies.project.repository.CustomerRepository;
import com.reshipies.project.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;


    @Override
    public Item saveItem(Item item) {
        item.setId(getNextItemId());
        return this.itemRepository.save(item);
    }
    @Override
    public long getNextItemId(){
        Query query = entityManager.createQuery("SELECT MAX(item.id) FROM Item item");
        List resultList = query.getResultList();
        Long maxId = (Long) resultList.get(0); // Assuming there's at least one order item
        if (maxId == null) {
            return 1L; // If no order items exist yet, start from 1
        } else {
            return maxId + 1; // Increment the highest ID
        }
    }

    public void updateItemPrice(Long itemId, double newPrice) throws IllegalArgumentException {
        Optional<Item> existingItemOptional = itemRepository.findById(Math.toIntExact((itemId)));
        if (existingItemOptional.isPresent()) {
            Item existingItem = existingItemOptional.get();
            existingItem.setPrice(newPrice);
            itemRepository.save(existingItem);
        } else {
            throw new IllegalArgumentException("Item not found");
        }
    }
}
