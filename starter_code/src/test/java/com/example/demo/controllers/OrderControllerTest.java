package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ObjectMapper mapper;

    private final String USERNAME = "phoint";
    private User user;
    private Cart cart;
    private UserOrder userOrder;
    List<UserOrder> orderList = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    Item item1;

    @Before
    public void setup() {
        user = new User();
        user.setId(10L);
        user.setUsername("phoint");

        item1 = new Item();
        item1.setId(10L);
        item1.setName("Round Widget");
        item1.setPrice(BigDecimal.valueOf(2.99));
        item1.setDescription("A widget that is round");
        items.add(item1);

        cart = new Cart();
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(item1.getPrice());
        cart.setUser(user);

        user.setCart(cart);
        userOrder = UserOrder.createFromCart(cart);
    }

    @Test
    public void submit() throws JsonProcessingException {
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserOrder order =  response.getBody();
        assertNotNull(order);
        assertEquals(order.getTotal(), user.getCart().getTotal());
        assertEquals(1, order.getItems().size());
        Item item = order.getItems().get(0);
        assertNotNull(item);
        assertEquals(item1.getName(), item.getName());
        assertEquals(item1.getDescription(), item.getDescription());
        assertEquals(item1.getPrice(), item.getPrice());

        User owner = order.getUser();
        assertNotNull(owner);
        assertEquals(user.getUsername(), owner.getUsername());
    }

    @Test
    public void submit_UserNotFound() throws JsonProcessingException {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void getOrdersForUser() {
        userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setTotal(cart.getTotal());
        userOrder.setItems(items);
        orderList.add(userOrder);

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orderList);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }

    @Test
    public void getOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}