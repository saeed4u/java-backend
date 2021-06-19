package com.glivion.backend.payload.dto;

import com.glivion.backend.domain.model.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class UserDto {

    private Integer id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;

}
