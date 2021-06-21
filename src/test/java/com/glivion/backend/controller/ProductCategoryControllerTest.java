package com.glivion.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glivion.backend.domain.model.ProductCategory;
import com.glivion.backend.payload.dto.product.ProductCategoryDto;
import com.glivion.backend.payload.dto.product.ProductDto;
import com.glivion.backend.service.product.ProductCategoryService;
import com.glivion.backend.service.product.ProductService;
import com.glivion.backend.util.DatabaseSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ProductCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseSeeder databaseSeeder;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(){
        databaseSeeder.createProductCategories();
    }

    @Test
    public void testGetCategories() throws Exception {

        List<ProductCategoryDto> productCategories = productCategoryService.getCategories();

        this.mockMvc
                .perform(
                        get("/api/v1/categories")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productCategories)));
    }

    @Test
    public void testGetCategory() throws Exception{
        ProductCategoryDto productCategory = productCategoryService.getCategories().stream().findAny().orElseThrow();

        this.mockMvc
                .perform(
                        get("/api/v1/categories/"+productCategory.getId())
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productCategory)));
    }

    @Test
    public void testGetCategoryProducts() throws Exception{
        ProductCategoryDto productCategory = productCategoryService.getCategories().stream().findAny().orElseThrow();
        List<ProductDto> productDtos = productService.getProductsOfCategory(productCategory.getId());
        this.mockMvc
                .perform(
                        get("/api/v1/categories/"+productCategory.getId()+"/products")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productDtos)));
    }

}
