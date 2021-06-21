package com.glivion.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.glivion.backend.domain.model.Order;
import com.glivion.backend.domain.model.Product;
import com.glivion.backend.domain.model.Role;
import com.glivion.backend.domain.model.User;
import com.glivion.backend.domain.repository.OrderRepository;
import com.glivion.backend.domain.repository.ProductRepository;
import com.glivion.backend.exception.handler.ApiError;
import com.glivion.backend.payload.dto.auth.AuthDto;
import com.glivion.backend.payload.dto.auth.TokenDto;
import com.glivion.backend.payload.dto.order.OrderDto;
import com.glivion.backend.payload.request.OrderItemRequest;
import com.glivion.backend.payload.request.OrderRequest;
import com.glivion.backend.payload.request.SignUpRequest;
import com.glivion.backend.service.UserService;
import com.glivion.backend.service.order.OrderService;
import com.glivion.backend.util.DatabaseSeeder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseSeeder databaseSeeder;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final String BASE_URL = "/api/v1/orders";

    String username = "username";
    String email = "testuser@test.com";
    String name = "Test User";
    String phoneNumber = "055505000";
    String password = "password";

    private AuthDto loginUser() {
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        return userService.createCustomer(signUpRequest);
    }

    @Test
    public void testGetOrdersExpectAuthenticationError() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMakeOrderExpectBadRequestError() throws Exception {
        AuthDto authDto = loginUser();

        mockMvc.perform(post(BASE_URL)
                .header("Authorization", "Bearer " + authDto.getToken().getToken())
        ).andExpect(status().isBadRequest());

    }

    @Test
    public void testMakeOrderExpectProductNotFoundError() throws Exception {
        AuthDto authDto = loginUser();

        OrderItemRequest orderItemRequest = new OrderItemRequest(1, 10);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        mockMvc.perform(post(BASE_URL)
                .header("Authorization", "Bearer " + authDto.getToken().getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void testMakeOrderExpectSuccess() throws Exception{
        AuthDto authDto = loginUser();
        databaseSeeder.createProductCategories();
        List<Product> products = productRepository.findAll();
        OrderItemRequest orderItemRequest = new OrderItemRequest(products.get(0).getId(), 10);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .header("Authorization", "Bearer " + authDto.getToken().getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        ).andExpect(status().isOk());

        OrderDto orderDto = orderService.getUserOrders(authDto.getUser().getId()).get(0);

        resultActions.andExpect(content().json(objectMapper.writeValueAsString(orderDto)));
    }

    @Test
    public void testGetUserOrders() throws Exception{
        AuthDto authDto = loginUser();
        databaseSeeder.createProductCategories();
        List<Product> products = productRepository.findAll();
        OrderItemRequest orderItemRequest = new OrderItemRequest(products.get(0).getId(), 10);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        mockMvc.perform(post(BASE_URL)
                .header("Authorization", "Bearer " + authDto.getToken().getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        ).andExpect(status().isOk());

        ResultActions resultActions = mockMvc.perform(get(BASE_URL)
                .header("Authorization", "Bearer " + authDto.getToken().getToken())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        List<OrderDto> orderDto = orderService.getUserOrders(authDto.getUser().getId());

        resultActions.andExpect(content().json(objectMapper.writeValueAsString(orderDto)));
    }



    @Test
    public void testGetOrderDetail() throws Exception{
        AuthDto authDto = loginUser();
        databaseSeeder.createProductCategories();
        List<Product> products = productRepository.findAll();
        OrderItemRequest orderItemRequest = new OrderItemRequest(products.get(0).getId(), 10);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        mockMvc.perform(post(BASE_URL)
                .header("Authorization", "Bearer " + authDto.getToken().getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest))
        ).andExpect(status().isOk());

        List<Order> orders = orderRepository.findAll();

        ResultActions resultActions = mockMvc.perform(get(BASE_URL+"/"+orders.get(0).getId())
                .header("Authorization", "Bearer " + authDto.getToken().getToken())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        OrderDto orderDto = orderService.getOrder(authDto.getUser().getId(), orders.get(0).getId());

        resultActions.andExpect(content().json(objectMapper.writeValueAsString(orderDto)));
    }



}
