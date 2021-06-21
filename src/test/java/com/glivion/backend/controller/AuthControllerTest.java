package com.glivion.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glivion.backend.payload.request.SignUpRequest;
import com.glivion.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthControllerTest {

    String username = "username";
    String email = "testuser@test.com";
    String name = "Test User";
    String phoneNumber = "055505000";
    String password = "password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Test
    public void shouldReturnUsernameValidationError() throws Exception{

        SignUpRequest signUpRequest = new SignUpRequest(null, name, email, phoneNumber, password);
        this.mockMvc
                .perform(
                        post("/api/v1/auth/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\n" +
                        "    \"status\": \"BAD_REQUEST\",\n" +
                        "    \"message\": \"Validation errors\",\n" +
                        "    \"subErrors\": {\n" +
                        "        \"username\": \"must not be blank\"\n" +
                        "    }\n" +
                        "}"));
    }

    @Test
    public void createEmployeeExpectSuccess() throws Exception{
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        this.mockMvc
                .perform(
                        post("/api/v1/auth/signup/customer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"token\": {\n" +
                        "        \"tokenType\": \"Bearer\",\n" +
                        "        \"ttl\": 54000\n" +
                        "    },\n" +
                        "    \"user\": {\n" +
                        "        \"name\": \""+name+"\",\n" +
                        "        \"email\": \""+ email+ "\",\n" +
                        "        \"phoneNumber\":\""+phoneNumber+"\",\n" +
                        "        \"role\": \"CUSTOMER\"\n" +
                        "    }\n" +
                        "}"));

    }
    @Test
    public void testSignInExpectSuccess() throws Exception{
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        userService.createCustomer(signUpRequest);
        this.mockMvc
                .perform(
                        post("/api/v1/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                        "    \"token\": {\n" +
                        "        \"tokenType\": \"Bearer\",\n" +
                        "        \"ttl\": 54000\n" +
                        "    },\n" +
                        "    \"user\": {\n" +
                        "        \"name\": \""+name+"\",\n" +
                        "        \"email\": \""+ email+ "\",\n" +
                        "        \"phoneNumber\":\""+phoneNumber+"\",\n" +
                        "        \"role\": \"CUSTOMER\"\n" +
                        "    }\n" +
                        "}"));
    }

}
