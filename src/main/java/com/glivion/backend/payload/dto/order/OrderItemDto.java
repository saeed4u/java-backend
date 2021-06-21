package com.glivion.backend.payload.dto.order;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class OrderItemDto {
    private Integer id;
    private String productName;
    private String productPrice;
    private Integer quantityBought;
    private String totalPrice;
    private LocalDateTime createdAt;
}
