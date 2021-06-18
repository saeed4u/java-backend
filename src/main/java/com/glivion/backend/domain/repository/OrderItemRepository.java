package com.glivion.backend.domain.repository;

import com.glivion.backend.domain.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {
}
