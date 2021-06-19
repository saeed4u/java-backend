package com.glivion.backend.payload.request;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.*;

@RequiredArgsConstructor
@Getter
public class SignUpRequest {

    @NotNull
    @NotBlank
    private final String username;
    @NotNull
    @NotBlank
    private final String name;
    @NotNull
    @NotBlank
    @Email
    private final String email;

    private final String phoneNumber;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 100)
    private final String password;

}
