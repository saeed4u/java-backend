package com.glivion.backend.controller;

import com.glivion.backend.payload.dto.order.OrderDto;
import com.glivion.backend.payload.request.OrderRequest;
import com.glivion.backend.service.UserDetailsImpl;
import com.glivion.backend.service.order.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public @ResponseBody OrderDto makeOrder(@RequestBody @Valid OrderRequest orderRequest){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.makeOrder(userDetails.getId(), orderRequest);
    }

    @GetMapping
    public @ResponseBody List<OrderDto> getUserOrders(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.getUserOrders(userDetails.getId());
    }

    @GetMapping("{orderId}")
    public @ResponseBody OrderDto getOrder(@PathVariable Integer orderId){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderService.getOrder(userDetails.getId(), orderId);
    }

}
