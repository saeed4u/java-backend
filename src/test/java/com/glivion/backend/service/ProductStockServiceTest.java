package com.glivion.backend.service;

import com.glivion.backend.domain.model.ProductStock;
import com.glivion.backend.domain.repository.ProductStockRepository;
import com.glivion.backend.exception.BadRequestException;
import com.glivion.backend.service.product.ProductStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductStockServiceTest {

    @Autowired
    private ProductStockService productStockService;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Test
    public void testProductStockQuantityLogic(){
        ProductStock productStock = new ProductStock();
        productStock.setAvailableQuantity(100);
        productStock.setQuantityOnHold(0);
        productStock.setTotalQuantity(100);

        productStock = productStockRepository.save(productStock);

        ProductStock finalProductStock = productStock;
        assertThrows(BadRequestException.class, () -> productStockService.deductQuantity(finalProductStock.getId(), 101));

        productStockService.deductQuantity(finalProductStock.getId(), 50);

        productStock = productStockRepository.findById(productStock.getId()).orElseThrow();
        assertThat(productStock.getAvailableQuantity()).isEqualTo(50);
        assertThat(productStock.getQuantityOnHold()).isEqualTo(50);
        assertThat(productStock.getTotalQuantity()).isEqualTo(100);


        productStockService.returnQuantity(productStock.getId(), 50);
        productStock = productStockRepository.findById(productStock.getId()).orElseThrow();
        assertThat(productStock.getAvailableQuantity()).isEqualTo(100);
        assertThat(productStock.getQuantityOnHold()).isEqualTo(0);
        assertThat(productStock.getTotalQuantity()).isEqualTo(100);


        productStockService.deductQuantity(finalProductStock.getId(), 50);

        productStockService.confirmQuantity(productStock.getId(), 50);
        productStock = productStockRepository.findById(productStock.getId()).orElseThrow();
        assertThat(productStock.getAvailableQuantity()).isEqualTo(50);
        assertThat(productStock.getQuantityOnHold()).isEqualTo(0);
        assertThat(productStock.getTotalQuantity()).isEqualTo(50);
    }

}
