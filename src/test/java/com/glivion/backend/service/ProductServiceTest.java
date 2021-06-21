package com.glivion.backend.service;

import com.glivion.backend.domain.model.Product;
import com.glivion.backend.domain.model.ProductCategory;
import com.glivion.backend.domain.model.ProductStock;
import com.glivion.backend.domain.repository.ProductCategoryRepository;
import com.glivion.backend.domain.repository.ProductRepository;
import com.glivion.backend.domain.repository.ProductStockRepository;
import com.glivion.backend.payload.dto.product.ProductDto;
import com.glivion.backend.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    ProductService productService;

    @BeforeEach
    public void createProducts(){
        ProductCategory productCategory = new ProductCategory();

        productCategory.setCode("a_code");
        productCategory.setName("a_name");
        productCategory = productCategoryRepository.save(productCategory);

        ProductStock productStock = new ProductStock();
        productStock.setAvailableQuantity(100);
        productStock.setQuantityOnHold(0);
        productStock.setTotalQuantity(100);
        productStock = productStockRepository.save(productStock);

        Product product = new Product();
        product.setCategory(productCategory);
        product.setStock(productStock);
        product.setPrice(1000);
        product.setName("product_name");
        product.setCode("product_code");
        productRepository.save(product);
    }

    @Test
    public void testGetProductsExpectSuccess(){
        List<ProductDto> productDtos = productService.getAllProducts();
        assertThat(productDtos).hasSize(1);

        ProductDto productDto = productDtos.get(0);

        assertThat(productDto.getCategory().getCode()).isEqualTo("a_code");
        assertThat(productDto.getCategory().getName()).isEqualTo("a_name");

        assertThat(productDto.getStock().getQuantityOnHold()).isEqualTo(0);
        assertThat(productDto.getStock().getTotalQuantity()).isEqualTo(100);
        assertThat(productDto.getStock().getQuantityAvailable()).isEqualTo(100);

        assertThat(productDto.getName()).isEqualTo("product_name");
        assertThat(productDto.getCode()).isEqualTo("product_code");
        assertThat(productDto.getPrice()).isEqualTo(10);

        productRepository.deleteAll();

        productDtos = productService.getAllProducts();
        assertThat(productDtos).hasSize(0);
    }

    @Test
    public void testGetProductsOfCategoriesExpectSuccess(){
        assertThrows(EntityNotFoundException.class, () -> productService.getProductsOfCategory(0));

        ProductCategory productCategory = productCategoryRepository.findByCode("a_code").orElseThrow();

        List<ProductDto> productDtos = productService.getProductsOfCategory(productCategory.getId());
        assertThat(productDtos).hasSize(1);
    }

}
