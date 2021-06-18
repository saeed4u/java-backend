package com.glivion.backend.domain.repository;

import com.glivion.backend.domain.model.ProductCategory;
import org.springframework.data.repository.CrudRepository;

public interface ProductCategoryRepository extends CrudRepository<ProductCategory, Integer> {
}
