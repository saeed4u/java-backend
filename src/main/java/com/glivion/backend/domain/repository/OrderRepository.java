package com.glivion.backend.domain.repository;

import com.glivion.backend.domain.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Integer> {
}
