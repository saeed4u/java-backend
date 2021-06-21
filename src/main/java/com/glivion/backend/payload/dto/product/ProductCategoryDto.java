package com.glivion.backend.payload.dto.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class ProductCategoryDto {

    private Integer id;
    private final String name;
    private final String code;
    private final String description;

}
