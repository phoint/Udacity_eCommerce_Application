package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CartControllerTest {
    @InjectMocks
    private CartController cartController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemRepository itemRepository;

    private ModifyCartRequest request;
    private User addToCartUser;

    List<Item> items = new ArrayList<>();
    Item item1;

    @Before
    public void setup() {
        request = new ModifyCartRequest();
        request.setUsername("phoint");
        request.setItemId(10L);
        request.setQuantity(1);

        addToCartUser = new User();
        addToCartUser.setId(10L);
        addToCartUser.setUsername("phoint");
        addToCartUser.setPassword("testPassword");
        addToCartUser.setCart(new Cart());


        item1 = new Item();
        item1.setId(10L);
        item1.setName("Round Widget");
        item1.setPrice(BigDecimal.valueOf(2.99));
        item1.setDescription("A widget that is round");

        items.add(item1);
    }

    @Test
    public void addTocart() {
        when(userRepository.findByUsername("phoint")).thenReturn(addToCartUser);
        when(itemRepository.findById(10L)).thenReturn(Optional.ofNullable(item1));

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(item1.getPrice(), cart.getTotal());
        assertNotNull(cart.getItems());
        List<Item> actualItems = cart.getItems();
        assertFalse(cart.getItems().isEmpty());
        Item actualItem = actualItems.get(0);
        assertEquals(item1.getName(), actualItem.getName());
        assertEquals(item1.getDescription(), actualItem.getDescription());
        assertEquals(item1.getPrice(), actualItem.getPrice());
    }

    @Test
    public void addTocart_NotFoundUser() {
        when(userRepository.findByUsername("phoint")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void addTocart_NotFoundItem() {
        when(userRepository.findByUsername("phoint")).thenReturn(addToCartUser);
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void removeFromcart() {
        User removeFromCartUser = addToCartUser;
        removeFromCartUser.getCart().setItems(items);
        removeFromCartUser.getCart().setTotal(item1.getPrice());
        removeFromCartUser.getCart().setUser(removeFromCartUser);

        when(userRepository.findByUsername("phoint")).thenReturn(removeFromCartUser);
        when(itemRepository.findById(10L)).thenReturn(Optional.ofNullable(item1));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(0,2), cart.getTotal());
        assertNotNull(cart.getItems());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void removeFromCart_NotFoundUser() {
        when(userRepository.findByUsername("phoint")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void removeFromCart_NotFoundItem() {
        when(userRepository.findByUsername("phoint")).thenReturn(addToCartUser);
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}