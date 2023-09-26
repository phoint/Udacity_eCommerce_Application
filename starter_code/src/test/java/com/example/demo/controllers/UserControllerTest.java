package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ObjectMapper mapper;

    private User expectedUser;
    @Before
    public void setUp() throws Exception {
        expectedUser = new User();
        expectedUser.setId(10L);
        expectedUser.setUsername("phoint");
        expectedUser.setPassword("testPassword");
    }

    @Test
    public void findById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(expectedUser));
        ResponseEntity<User> response = userController.findById(10L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("phoint", user.getUsername());
        assertEquals("testPassword", user.getPassword());
    }

    @Test
    public void findById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        ResponseEntity<User> response = userController.findById(10L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void findByUserName() {
        when(userRepository.findByUsername(any())).thenReturn(expectedUser);
        ResponseEntity<User> response = userController.findByUserName("phoint");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("phoint", user.getUsername());
        assertEquals("testPassword", user.getPassword());
    }

    @Test
    public void findByUserName_NotFound() {
        when(userRepository.findByUsername(any())).thenReturn(null);
        ResponseEntity<User> response = userController.findByUserName("phoint");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void createUser() throws JsonProcessingException {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Phoint");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("testPassword");
        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("Phoint", user.getUsername());
        assertEquals("testPassword", user.getPassword());
    }

    @Test
    public void createUser_PasswordTooShort() throws JsonProcessingException {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Phoint");
        request.setPassword("pass");
        request.setConfirmPassword("pass");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void createUser_IncorrectPasswordConfirm() throws JsonProcessingException {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Phoint");
        request.setPassword("testPassword");
        request.setConfirmPassword("incorrectPassword");

        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }
}