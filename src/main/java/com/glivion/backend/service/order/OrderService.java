package com.glivion.backend.service.order;

import com.glivion.backend.domain.model.*;
import com.glivion.backend.domain.repository.OrderItemRepository;
import com.glivion.backend.domain.repository.OrderRepository;
import com.glivion.backend.domain.repository.ProductRepository;
import com.glivion.backend.domain.repository.UserRepository;
import com.glivion.backend.exception.BadRequestException;
import com.glivion.backend.exception.UnAuthorisedException;
import com.glivion.backend.payload.dto.order.OrderDto;
import com.glivion.backend.payload.dto.order.OrderItemDto;
import com.glivion.backend.payload.request.OrderItemRequest;
import com.glivion.backend.payload.request.OrderRequest;
import com.glivion.backend.service.product.ProductCategoryService;
import com.glivion.backend.service.product.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.glivion.backend.util.Converters.convertCentToActual;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductStockService productStockService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderDto makeOrder(int userId, OrderRequest orderRequest) {
        List<OrderItemRequest> orderItemRequests = orderRequest.getItems();
        Map<Integer, Product> products = orderItemRequests.stream().map(this::toProduct).collect(Collectors.toMap(Product::getId, Function.identity()));
        User user = userRepository.findById(userId).orElseThrow();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setUser(user);
        AtomicInteger orderTotalAtomic = new AtomicInteger(0);
        AtomicInteger groceriesTotalAtomic = new AtomicInteger(0);

        Set<OrderItem> orderItems = getOrderItems(orderItemRequests, products, orderTotalAtomic, groceriesTotalAtomic);

        order.setOrderItems(orderItems);

        int groceriesTotal = groceriesTotalAtomic.get();
        int subTotal = orderTotalAtomic.get();
        int totalToApplyPercentageDiscount = subTotal - groceriesTotal;
        int percentageDiscount = (int) (totalToApplyPercentageDiscount * getPercentageDiscountToApply(user));
        int otherDiscount = getOtherDiscountToApply(subTotal);
        int discountToApply = percentageDiscount + otherDiscount;
        int orderTotal = subTotal - discountToApply;

        order.setOrderTotal(orderTotal);
        order.setPercentageDiscount(percentageDiscount);
        order.setDiscount(discountToApply);
        order.setOtherDiscount(otherDiscount);
        order.setSubTotal(subTotal);
        order.setInvoiceNumber(LocalDateTime.now().getYear() + "-" + RandomStringUtils.random(6, true, true));
        Order savedOrder = orderRepository.save(order);
        orderItems.forEach((orderItem) -> orderItem.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        orderRepository.flush();
        return toOrderDto(order);
    }

    public List<OrderDto> getUserOrders(int userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toOrderDto).collect(Collectors.toList());
    }

    public OrderDto getOrder(Integer userId, Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        if (!order.getUser().getId().equals(userId)) {
            throw new UnAuthorisedException();
        }
        return toOrderDto(order);
    }

    private Set<OrderItem> getOrderItems(List<OrderItemRequest> orderItemRequests, Map<Integer, Product> products, AtomicInteger orderTotalAtomic, AtomicInteger groceriesTotalAtomic) {
        return orderItemRequests.stream().map((OrderItemRequest orderItemRequest) -> {
            int productId = orderItemRequest.getProductId();
            int quantity = orderItemRequest.getQuantity();
            Product product = products.get(productId);
            productStockService.deductQuantity(product.getStock().getId(), quantity);

            int itemTotal = product.getPrice() * quantity;
            if (Objects.equals(product.getCategory().getCode(), ProductCategoryService.GROCERIES_CATEGORY_CODE)) {
                groceriesTotalAtomic.addAndGet(itemTotal);
            }
            orderTotalAtomic.addAndGet(itemTotal);
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(quantity);
            orderItem.setProduct(product);
            orderItem.setTotalPrice(itemTotal);

            return orderItem;
        }).collect(Collectors.toSet());
    }

    private double getPercentageDiscountToApply(User user) {
        switch (user.getRole()) {
            case CUSTOMER:
                return getCustomerDiscountPercentage(user);
            case AFFILIATE:
                return 0.1;
            default:
                return 0.3;
        }
    }

    public int getOtherDiscountToApply(int total) {
        int value = (int) (total / 100D / 100D);
        return value * 5 * 100;
    }

    private double getCustomerDiscountPercentage(User user) {
        LocalDateTime now = LocalDateTime.now();
        long years = user.getJoinedAt().until(now, ChronoUnit.YEARS);

        if (years >= 2) {
            return 0.05;
        }
        return 0;
    }

    private Product toProduct(OrderItemRequest orderItemRequest) {
        return productRepository.findById(orderItemRequest.getProductId()).orElseThrow(() -> new BadRequestException("Product with id " + orderItemRequest.getProductId() + " was not found"));
    }

    private OrderDto toOrderDto(Order order) {
        int totalDiscount = order.getDiscount();
        int percentageDiscount = order.getPercentageDiscount();
        int otherDiscount = totalDiscount - percentageDiscount;

        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream().map(this::toOrderItemDto).collect(Collectors.toList());

        return OrderDto.of(order.getId(), order.getInvoiceNumber(), convertCentToActual(order.getSubTotal()), convertCentToActual(totalDiscount), convertCentToActual(percentageDiscount), convertCentToActual(otherDiscount), convertCentToActual(order.getOrderTotal()), order.getStatus(), order.getCreatedAt(), orderItemDtos);
    }

    private OrderItemDto toOrderItemDto(OrderItem orderItem) {
        return OrderItemDto.of(orderItem.getId(), orderItem.getProduct().getName(), convertCentToActual(orderItem.getProduct().getPrice()), orderItem.getQuantity(), convertCentToActual(orderItem.getTotalPrice()), orderItem.getCreatedAt());
    }
}
