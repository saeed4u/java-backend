package com.glivion.backend.service;

import com.glivion.backend.domain.model.Role;
import com.glivion.backend.exception.BadRequestException;
import com.glivion.backend.payload.dto.AuthDto;
import com.glivion.backend.payload.dto.TokenDto;
import com.glivion.backend.payload.dto.UserDto;
import com.glivion.backend.payload.request.SignInRequest;
import com.glivion.backend.payload.request.SignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    String username = "username";
    String email = "testuser@test.com";
    String name = "Test User";
    String phoneNumber = "055505000";
    String password = "password";

    @Test
    public void testCreateCustomerExpectSuccess() {

        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);

        AuthDto authDto = userService.createCustomer(signUpRequest);
        makeAssertions(authDto, Role.CUSTOMER);
    }

    @Test
    public void testCreateAffiliateExpectSuccess(){
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);

        AuthDto authDto = userService.createAffiliate(signUpRequest);
        makeAssertions(authDto, Role.AFFILIATE);
    }

    @Test
    public void testCreateEmployeeExpectSuccess(){
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);

        AuthDto authDto = userService.createEmployee(signUpRequest);
        makeAssertions(authDto, Role.EMPLOYEE);
    }

    @Test
    public void testCreateUserExpectValidationErrors(){
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        AuthDto authDto = userService.createAffiliate(signUpRequest);
        assertThat(authDto).isNotNull();
        Throwable throwable = assertThrows(BadRequestException.class, () -> userService.createAffiliate(signUpRequest));
        assertThat(throwable.getMessage()).isEqualTo("A user with the same username exists");

        SignUpRequest newSignUpRequest = new SignUpRequest("new user", name, email, phoneNumber, password);
        throwable = assertThrows(BadRequestException.class, () -> userService.createAffiliate(newSignUpRequest));
        assertThat(throwable.getMessage()).isEqualTo("A user with the same email address exists");

        SignUpRequest newSignUpRequestDifferentPhoneNumber = new SignUpRequest("new user", name, "new@email.com", phoneNumber, password);
        throwable = assertThrows(BadRequestException.class, () -> userService.createAffiliate(newSignUpRequestDifferentPhoneNumber));
        assertThat(throwable.getMessage()).isEqualTo("A user with the same phone number exists");
    }

    @Test
    public void testSignInExpectSuccess(){
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        userService.createAffiliate(signUpRequest);
        SignInRequest signInRequest = new SignInRequest(username, password);
        AuthDto authDto = userService.signInUser(signInRequest);
        makeAssertions(authDto, Role.AFFILIATE);
    }

    @Test
    public void testSignInExpectFailure(){
        SignUpRequest signUpRequest = new SignUpRequest(username, name, email, phoneNumber, password);
        userService.createAffiliate(signUpRequest);
        SignInRequest signInRequest = new SignInRequest(username, "bad_password");
        assertThrows(AuthenticationException.class,()->userService.signInUser(signInRequest));
    }

    private void makeAssertions(AuthDto authDto, Role role) {
        assertThat(authDto).isNotNull();

        UserDto userDto = authDto.getUser();
        TokenDto tokenDto = authDto.getToken();
        assertThat(userDto).isNotNull();
        assertThat(tokenDto).isNotNull();

        assertThat(userDto.getRole()).isEqualTo(role);
        assertThat(userDto.getEmail()).isEqualTo(email);
        assertThat(userDto.getPhoneNumber()).isEqualTo(phoneNumber);

        assertThat(tokenDto.getToken()).isNotNull();
        assertThat(tokenDto.getTokenType()).isEqualTo("Bearer");
    }

}
