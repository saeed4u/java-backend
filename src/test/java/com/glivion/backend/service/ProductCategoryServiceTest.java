package com.glivion.backend.service;


import com.glivion.backend.domain.model.ProductCategory;
import com.glivion.backend.domain.repository.ProductCategoryRepository;
import com.glivion.backend.payload.dto.product.ProductCategoryDto;
import com.glivion.backend.service.product.ProductCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductCategoryServiceTest {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @BeforeEach
    public void createProductCategories(){
        List<ProductCategory> productCategories = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            ProductCategory productCategory = new ProductCategory();
            productCategory.setId(i + 1);
            productCategory.setName("name "+i);
            productCategory.setDescription("description "+i);
            productCategory.setCode("code "+i);
            productCategories.add(productCategory);
        }
        productCategoryRepository.saveAll(productCategories);
    }

    @Test
    public void testGetProductCategoriesExpectSuccess(){
        List<ProductCategoryDto> productCategoryDtos = productCategoryService.getCategories();
        assertThat(productCategoryDtos).hasSize(4);
        for (int i = 0; i < 4; i++){
            ProductCategoryDto productCategory = productCategoryDtos.get(i);
            assertThat(productCategory.getName()).isEqualTo("name "+i);
            assertThat(productCategory.getDescription()).isEqualTo("description "+i);
            assertThat(productCategory.getCode()).isEqualTo("code "+i);
        }
    }

    @Test
    public void testGetProductCategoryExpectSuccess(){
        ProductCategoryDto productCategoryDto = productCategoryService.getCategory(1);
        assertThat(productCategoryDto).isNotNull();
        assertThat(productCategoryDto.getName()).isEqualTo("name "+0);
        assertThat(productCategoryDto.getDescription()).isEqualTo("description "+0);
        assertThat(productCategoryDto.getCode()).isEqualTo("code "+0);
    }

}
