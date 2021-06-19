package com.glivion.backend.payload.request;

import lombok.*;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true,access = AccessLevel.PRIVATE)
@Getter
public class SignInRequest {

    private final String username;
    private final String password;

}
