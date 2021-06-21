package com.glivion.backend.domain.repository;

import com.glivion.backend.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(int userId);

}
