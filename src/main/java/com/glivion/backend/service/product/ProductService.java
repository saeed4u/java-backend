package com.glivion.backend.service.product;

import com.glivion.backend.domain.model.Product;
import com.glivion.backend.domain.repository.ProductRepository;
import com.glivion.backend.payload.dto.product.ProductCategoryDto;
import com.glivion.backend.payload.dto.product.ProductDto;
import com.glivion.backend.payload.dto.product.ProductStockDto;
import com.glivion.backend.util.Converters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryService productCategoryService;

    public List<ProductDto> getAllProducts(){
        return toProductDto(productRepository.findAll());
    }

    public List<ProductDto> getProductsOfCategory(Integer categoryId){
       ProductCategoryDto productCategoryDto = productCategoryService.getCategory(categoryId);
        List<Product> products = productRepository.findByCategoryId(productCategoryDto.getId());
        return toProductDto(products);
    }

    private List<ProductDto> toProductDto(List<Product> products){
        return products.stream().map((product) -> {
            ProductCategoryDto category = ProductCategoryDto.of(product.getCategory().getId(), product.getCategory().getName(), product.getCategory().getCode(), product.getCategory().getDescription());
            ProductStockDto stock = ProductStockDto.of(product.getStock().getAvailableQuantity(), product.getStock().getTotalQuantity(), product.getStock().getQuantityOnHold());

            return ProductDto.of(product.getId(),product.getName(), product.getCode(),
                    product.getDescription(), product.getImageUrl(), Converters.convertCentToActual(product.getPrice()), category, stock);

        }).collect(Collectors.toList());
    }

}
