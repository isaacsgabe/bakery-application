package com.reshipies.project.repository;

import com.reshipies.project.model.CustomerOrder;
import com.reshipies.project.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem,Integer> {
}