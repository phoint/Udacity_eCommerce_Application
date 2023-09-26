package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.BeforeClass;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemRepository itemRepository;

    List<Item> items = new ArrayList<>();
    Item item1;
    @Before
    public void setUp() {
        item1 = new Item();
        item1.setId(10L);
        item1.setName("Round Widget");
        item1.setPrice(BigDecimal.valueOf(2.99));
        item1.setDescription("A widget that is round");

        items.add(item1);
    }

    @Test
    public void getItems() {
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> actualList = response.getBody();

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        Item itemActual = actualList.get(0);

        assertNotNull(itemActual);
        assertEquals(Long.valueOf(10), itemActual.getId());
        assertEquals(BigDecimal.valueOf(2.99), itemActual.getPrice());
        assertEquals("Round Widget", itemActual.getName());
        assertEquals("A widget that is round", itemActual.getDescription());
    }

    @Test
    public void getItemById() {
        when(itemRepository.findById(10L)).thenReturn(Optional.ofNullable(item1));
        ResponseEntity<Item> response = itemController.getItemById(10L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item itemActual = response.getBody();

        assertNotNull(itemActual);
        assertEquals(Long.valueOf(10), itemActual.getId());
        assertEquals(BigDecimal.valueOf(2.99), itemActual.getPrice());
        assertEquals("Round Widget", itemActual.getName());
        assertEquals("A widget that is round", itemActual.getDescription());
    }

    @Test
    public void getItemsByName() {
        when(itemRepository.findByName("Round Widget")).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> actualList = response.getBody();

        assertNotNull(actualList);
        assertEquals(1, actualList.size());
        Item itemActual = actualList.get(0);

        assertNotNull(itemActual);
        assertEquals(Long.valueOf(10), itemActual.getId());
        assertEquals(BigDecimal.valueOf(2.99), itemActual.getPrice());
        assertEquals("Round Widget", itemActual.getName());
        assertEquals("A widget that is round", itemActual.getDescription());
    }
}