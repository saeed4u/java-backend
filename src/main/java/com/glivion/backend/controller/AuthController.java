package com.glivion.backend.controller;

import com.glivion.backend.payload.dto.auth.AuthDto;
import com.glivion.backend.payload.request.SignInRequest;
import com.glivion.backend.payload.request.SignUpRequest;
import com.glivion.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signin")
    public @ResponseBody AuthDto authenticateUser(@Valid @RequestBody SignInRequest signInRequest){
        return userService.signInUser(signInRequest);
    }

    @PostMapping("/signup/customer")
    public  @ResponseBody AuthDto signUpCustomer(@Valid @RequestBody SignUpRequest signUpRequest){
        return userService.createCustomer(signUpRequest);
    }

    @PostMapping("/signup/affiliate")
    public  @ResponseBody AuthDto signUpAffiliate(@Valid @RequestBody SignUpRequest signUpRequest){
        return userService.createAffiliate(signUpRequest);
    }

    @PostMapping("/signup/employee")
    public  @ResponseBody AuthDto signUpEmployee(@Valid @RequestBody SignUpRequest signUpRequest){
        return userService.createEmployee(signUpRequest);
    }

}
