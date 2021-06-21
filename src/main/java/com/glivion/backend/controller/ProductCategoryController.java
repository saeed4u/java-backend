package com.glivion.backend.controller;

import com.glivion.backend.payload.dto.product.ProductCategoryDto;
import com.glivion.backend.payload.dto.product.ProductDto;
import com.glivion.backend.service.product.ProductCategoryService;
import com.glivion.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;
    private final ProductService productService;

    @GetMapping
    public @ResponseBody
    List<ProductCategoryDto> getCategories() {
        return productCategoryService.getCategories();
    }

    @GetMapping("{categoryId}")
    public @ResponseBody
    ProductCategoryDto getCategory(@PathVariable Integer categoryId) {
        return productCategoryService.getCategory(categoryId);
    }

    @GetMapping("{categoryId}/products")
    public @ResponseBody
    List<ProductDto> getCategoryProducts(@PathVariable Integer categoryId) {
        return productService.getProductsOfCategory(categoryId);
    }


}
