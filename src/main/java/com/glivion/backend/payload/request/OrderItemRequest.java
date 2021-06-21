package com.glivion.backend.payload.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RequiredArgsConstructor
@Getter
public class OrderItemRequest {

    @NotNull
    private Integer productId;

    @NotNull
    @Size(min = 1, max =  10)
    private Integer quantity;
}
