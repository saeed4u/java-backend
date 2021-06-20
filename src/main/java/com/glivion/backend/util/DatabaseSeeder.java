package com.glivion.backend.util;

import com.github.javafaker.Commerce;
import com.github.javafaker.Faker;
import com.glivion.backend.domain.model.Product;
import com.glivion.backend.domain.model.ProductCategory;
import com.glivion.backend.domain.model.ProductStock;
import com.glivion.backend.domain.repository.ProductCategoryRepository;
import com.glivion.backend.domain.repository.ProductRepository;
import com.glivion.backend.domain.repository.ProductStockRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private static final int [] prices = {1000, 500, 5500, 2000, 6003, 1003, 3002, 9864, 9034};


    private final ProductCategoryRepository productCategoryRepository;

    private final ProductRepository productRepository;

    private final ProductStockRepository productStockRepository;


    @Override
    public void run(String... args) throws Exception {
        createProductCategories();
    }

    private void createProductCategories(){
        Faker faker = new Faker();
        Commerce commerce = faker.commerce();
        for (int i = 0; i < 10; i++) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setCode(commerce.department()+ " - "+ i);
            productCategory.setName(commerce.material());
            productCategory = productCategoryRepository.save(productCategory);
            createProducts(faker, productCategory);
        }
    }

    private void createProducts(Faker faker, ProductCategory productCategory){
        Commerce commerce = faker.commerce();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setCode(commerce.promotionCode());
            product.setName(commerce.productName());
            product.setCategory(productCategory);
            product.setPrice(prices[random.nextInt(prices.length - 1)]);
            product.setStock(createProductStock());
            productRepository.save(product);
        }
    }

    private ProductStock createProductStock() {
        ProductStock productStock = new ProductStock();
        productStock.setAvailableQuantity(100);
        productStock.setQuantityOnHold(0);
        productStock.setTotalQuantity(100);
        return productStockRepository.save(productStock);
    }
}
