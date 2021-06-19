package com.glivion.backend.payload.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class AuthDto {
    private final TokenDto token;
    private final UserDto user;

}
