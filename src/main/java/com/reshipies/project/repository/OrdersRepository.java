package com.reshipies.project.repository;

import com.reshipies.project.model.Item;
import com.reshipies.project.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<CustomerOrder,Integer> {
}

