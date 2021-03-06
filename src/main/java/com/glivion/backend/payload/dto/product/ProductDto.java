package com.glivion.backend.payload.dto.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class ProductDto {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private String imageUrl;
    private String price;
    private ProductCategoryDto category;
    private ProductStockDto stock;
}
