package com.glivion.backend.domain.repository;

import com.glivion.backend.domain.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
