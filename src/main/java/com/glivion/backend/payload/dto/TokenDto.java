package com.glivion.backend.payload.dto;

import com.glivion.backend.security.JWTTokenUtil;
import lombok.*;

@AllArgsConstructor
@Getter
public class TokenDto {

    private final String token;
    private final String tokenType = "Bearer";
    private final long ttl = JWTTokenUtil.TOKEN_VALIDITY;

}
