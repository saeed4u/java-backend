package com.glivion.backend.payload.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class SignInRequest {

    @NotNull
    @NotBlank
    private final String username;

    @NotNull
    @NotBlank
    private final String password;

}
