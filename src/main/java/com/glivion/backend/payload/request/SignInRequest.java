package com.glivion.backend.payload.request;

import lombok.*;

@AllArgsConstructor
@Getter
public class SignInRequest {

    private final String username;
    private final String password;

}
