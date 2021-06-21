package com.glivion.backend.controller;

import com.glivion.backend.payload.dto.product.ProductDto;
import com.glivion.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public @ResponseBody List<ProductDto> getProducts(){
        return productService.getAllProducts();
    }

}
