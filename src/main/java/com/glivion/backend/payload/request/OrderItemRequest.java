package com.glivion.backend.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class OrderItemRequest {

    @NotNull
    private final Integer productId;

    @NotNull
    @Size(min = 1, max =  10)
    private final Integer quantity;
}
