package com.glivion.backend.domain.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Table(name = "orders")
@Entity
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(unique = true)
    String invoiceNumber;

    @ManyToOne
    User user;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    Set<OrderItem> orderItems;

    @Column(nullable = false)
    Integer subTotal;
    @Column(nullable = false)
    Integer discount;
    @Column(nullable = false)
    Integer percentageDiscount;
    @Column(nullable = false)
    Integer otherDiscount;
    @Column(nullable = false)
    Integer orderTotal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @CreationTimestamp
    @Column
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column
    LocalDateTime updatedAt;

}
