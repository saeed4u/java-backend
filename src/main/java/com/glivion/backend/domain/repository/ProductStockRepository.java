package com.glivion.backend.domain.repository;

import com.glivion.backend.domain.model.ProductStock;
import org.springframework.data.repository.CrudRepository;

public interface ProductStockRepository extends CrudRepository<ProductStock, Integer> {
}
