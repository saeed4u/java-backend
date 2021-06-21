package com.glivion.backend.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @ManyToOne
    Order order;
    @ManyToOne(fetch = FetchType.EAGER)
    Product product;

    Integer quantity;
    Integer totalPrice;


    @CreationTimestamp
    @Column(nullable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;
}
