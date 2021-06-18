package com.glivion.backend.domain.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "orders")
@Entity
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(unique = true, nullable = false)
    String invoiceNumber;

    @ManyToOne
    User user;

    @Column(nullable = false)
    Integer subTotal;
    @Column(nullable = false)
    Integer discount;
    @Column(nullable = false)
    Integer orderTotal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    LocalDateTime updatedAt;

}
