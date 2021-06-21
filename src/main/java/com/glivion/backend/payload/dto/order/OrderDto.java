package com.glivion.backend.payload.dto.order;

import com.glivion.backend.domain.model.OrderItem;
import com.glivion.backend.domain.model.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class OrderDto {

    private Integer id;
    private String invoiceNumber;
    private String subTotal;
    private String totalDiscount;
    private String percentageDiscount;
    private String otherDiscount;
    private String orderTotal;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

}
