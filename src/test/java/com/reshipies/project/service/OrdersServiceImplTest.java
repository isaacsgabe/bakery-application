package com.reshipies.project.service;

import com.reshipies.project.model.Customer;
import com.reshipies.project.model.CustomerOrder;
import com.reshipies.project.model.OrderItem;
import com.reshipies.project.repository.CustomerRepository;
import com.reshipies.project.repository.OrdersRepository;
import com.reshipies.project.repository.OrderItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrdersServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderItemsRepository orderItemsRepository;

    @InjectMocks
    private OrdersServiceImpl ordersService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveCustomer() {
        // Prepare data
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");

        // Mock repository method
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Call service method
        Long customerId = ordersService.saveCustomer(customer);

        // Verify repository method is called with correct parameter
        verify(customerRepository, times(1)).save(customer);

        // Verify return value
        assertEquals(customer.getId(), customerId);
    }

    @Test
    public void testSaveOrder() {
        // Prepare data
        CustomerOrder order = new CustomerOrder();
        order.setOrder_id(1L);

        // Call service method
        String result = ordersService.saveOrder(order);

        // Verify repository method is called with correct parameter
        verify(ordersRepository, times(1)).save(order);

        // Verify return value
        assertEquals("saved this order", result);
    }

    @Test
    public void testSaveOrderItems() {
        // Prepare data
        List<OrderItem> items = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        // Set item properties
        items.add(item1);

        // Call service method
        String result = ordersService.saveOrderItems(items);

        // Verify repository method is called with correct parameter
        verify(orderItemsRepository, times(1)).saveAll(items);

        // Verify return value
        assertEquals("saved all items in the order", result);
    }

    // You can write more tests for other methods as needed
}
