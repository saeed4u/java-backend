package com.glivion.backend.domain.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "product_stocks")
@Entity
@Getter
@Setter
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @OneToOne
    Product product;

    Integer availableQuantity;
    Integer quantityOnHold;
    Integer totalQuantity;


    @CreationTimestamp
    @Column(nullable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

}
