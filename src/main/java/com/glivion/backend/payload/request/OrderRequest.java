package com.glivion.backend.payload.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class OrderRequest {
    @NotNull
    @Size.List({@Size(min = 1, max = 10)})
    private List<OrderItemRequest> items;
}
