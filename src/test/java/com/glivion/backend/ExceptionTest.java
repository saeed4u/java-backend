package com.glivion.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glivion.backend.payload.request.SignInRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    String username = "username";
    String email = "testuser@test.com";
    String name = "Test User";
    String phoneNumber = "055505000";
    String password = "password";

    @Test
    public void testBadRequestException() throws Exception {

        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        userService.createCustomer(signUpRequest);

        this.mockMvc
                .perform(
                        post("/api/v1/auth/signup/customer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testHttpMessageNotReadableException() throws Exception {

        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        userService.createCustomer(signUpRequest);

        this.mockMvc
                .perform(
                        post("/api/v1/auth/signup/customer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"username\": \"braseed\",\n" +
                                        "    \"name\": \"Saeed Issah\",\n" +
                                        "    \"email\": \"saeedissah1@gmail.com\",\n" +
                                        "    \"password\": \"12345678\",\n" +
                                        "}")
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testMethodNotAllowedException() throws Exception {

        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        userService.createCustomer(signUpRequest);

        this.mockMvc
                .perform(
                        get("/api/v1/auth/signup/customer")
                )
                .andExpect(status().isMethodNotAllowed());

    }

    @Test
    public void testHandleInternalException() throws Exception {

        SignInRequest signInRequest = new SignInRequest(username, password);

        this.mockMvc
                .perform(
                        post("/api/v1/auth/signin")
                        .content(objectMapper.writeValueAsString(signInRequest))
                )
                .andExpect(status().isInternalServerError());

    }

}
