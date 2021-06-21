package com.glivion.backend.service;

import com.glivion.backend.domain.model.*;
import com.glivion.backend.domain.repository.ProductCategoryRepository;
import com.glivion.backend.domain.repository.ProductRepository;
import com.glivion.backend.domain.repository.UserRepository;
import com.glivion.backend.payload.dto.order.OrderDto;
import com.glivion.backend.payload.request.OrderItemRequest;
import com.glivion.backend.payload.request.OrderRequest;
import com.glivion.backend.service.order.OrderService;
import com.glivion.backend.service.product.ProductCategoryService;
import com.glivion.backend.util.Converters;
import com.glivion.backend.util.DatabaseSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityNotFoundException;

import static com.glivion.backend.service.order.OrderService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DatabaseSeeder databaseSeeder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void testCalculateOrderDiscount() {
        int otherDiscount = orderService.getOtherDiscountToApply(990 * 100);
        assertThat(otherDiscount).isEqualTo(4500);
    }

    @Test
    public void testPercentageDiscountToApply() {
        User user = new User();
        user.setUsername("a_username");
        user.setRole(Role.EMPLOYEE);
        user.setPassword("a_password");

        assertThat(orderService.getPercentageDiscountToApply(user)).isEqualTo(EMPLOYEE_DISCOUNT);

        user.setRole(Role.AFFILIATE);
        assertThat(orderService.getPercentageDiscountToApply(user)).isEqualTo(AFFILIATE_DISCOUNT);

        user.setRole(Role.CUSTOMER);
        user = userRepository.save(user);
        assertThat(orderService.getPercentageDiscountToApply(user)).isEqualTo(0);

        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
        user.setJoinedAt(twoYearsAgo);
        userRepository.save(user);
        user = userRepository.findById(user.getId()).orElseThrow();
        assertThat(orderService.getPercentageDiscountToApply(user)).isEqualTo(OLD_CUSTOMER_DISCOUNT);

    }

    @Test
    public void testMakeOrderExpectProductValidationFailure() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(12, 5);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        assertThrows(EntityNotFoundException.class, () -> orderService.makeOrder(1, orderRequest));
    }

    @Test
    public void testMakeOrderExpectUserValidationFailure() {
        databaseSeeder.createProductCategories();
        OrderItemRequest orderItemRequest = new OrderItemRequest(1, 5);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        assertThrows(EntityNotFoundException.class, () -> orderService.makeOrder(1, orderRequest));
    }

    @Test
    public void testMakeOrderCustomerExpectSuccess(){
        databaseSeeder.createProductCategories();

        User user = new User();
        user.setUsername("a_username");
        user.setRole(Role.CUSTOMER);
        user.setPassword("a_password");
        user = userRepository.save(user);

        int productPrice = 50 * 100;
        int quantity = 10;
        List<Product> products = productRepository.findAll();
        Product product = products.get(0);
        product.setPrice(productPrice);
        product = productRepository.save(product);


        OrderItemRequest orderItemRequest = new OrderItemRequest(product.getId(), quantity);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        OrderDto orderDto = orderService.makeOrder(user.getId(), orderRequest);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getItems()).hasSize(1);

        int subTotal = productPrice * quantity;
        int otherDiscount = 25 * 100;
        int orderTotal = subTotal - otherDiscount;

        makeOrderAssertions(orderDto, subTotal, otherDiscount,0, orderTotal);

        user.setJoinedAt(LocalDateTime.now().minusYears(2));
        user = userRepository.save(user);
        orderDto = orderService.makeOrder(user.getId(), orderRequest);
        int percentageDiscount = (int)(OLD_CUSTOMER_DISCOUNT * subTotal);
        orderTotal = subTotal - otherDiscount - percentageDiscount;
        makeOrderAssertions(orderDto, subTotal, otherDiscount,percentageDiscount,orderTotal);
    }

    @Test
    public void testMakeOrderEmployeeExpectSuccess(){
        databaseSeeder.createProductCategories();

        User user = new User();
        user.setUsername("a_username");
        user.setRole(Role.EMPLOYEE);
        user.setPassword("a_password");
        user = userRepository.save(user);

        int productPrice = 40 * 100;
        int quantity = 8;
        List<Product> products = productRepository.findAll();
        Product product = products.get(0);
        product.setPrice(productPrice);
        product = productRepository.save(product);


        OrderItemRequest orderItemRequest = new OrderItemRequest(product.getId(), quantity);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        OrderDto orderDto = orderService.makeOrder(user.getId(), orderRequest);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getItems()).hasSize(1);

        int subTotal = productPrice * quantity;
        int otherDiscount = 15 * 100;
        int percentageDiscount = (int) (EMPLOYEE_DISCOUNT * subTotal);
        int orderTotal = subTotal - otherDiscount - percentageDiscount;

        makeOrderAssertions(orderDto, subTotal, otherDiscount,percentageDiscount, orderTotal); }

    @Test
    public void testMakeOrderAffiliateExpectSuccess(){
        databaseSeeder.createProductCategories();

        User user = new User();
        user.setUsername("a_username");
        user.setRole(Role.AFFILIATE);
        user.setPassword("a_password");
        user = userRepository.save(user);

        int productPrice = 38 * 100;
        int quantity = 6;
        List<Product> products = productRepository.findAll();
        Product product = products.get(0);
        product.setPrice(productPrice);
        product = productRepository.save(product);


        OrderItemRequest orderItemRequest = new OrderItemRequest(product.getId(), quantity);
        OrderRequest orderRequest = new OrderRequest(Collections.singletonList(orderItemRequest));

        OrderDto orderDto = orderService.makeOrder(user.getId(), orderRequest);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getItems()).hasSize(1);

        int subTotal = productPrice * quantity;
        int otherDiscount = 10 * 100;
        int percentageDiscount = (int) (AFFILIATE_DISCOUNT * subTotal);
        int orderTotal = subTotal - otherDiscount - percentageDiscount;

        makeOrderAssertions(orderDto, subTotal, otherDiscount,percentageDiscount, orderTotal); }

    @Test
    public void testMakeOrderUserWithGroceryProductExpectSuccess(){
        databaseSeeder.createProductCategories();

        User user = new User();
        user.setUsername("a_username");
        user.setRole(Role.AFFILIATE);
        user.setPassword("a_password");
        user = userRepository.save(user);

        int productPrice = 38 * 100;
        int quantity = 6;
        List<Product> products = productRepository.findAll();
        Product product = products.get(0);
        product.setPrice(productPrice);
        ProductCategory productCategory = product.getCategory();
        productCategory.setCode(ProductCategoryService.GROCERIES_CATEGORY_CODE);
        productCategoryRepository.save(productCategory);
        product = productRepository.save(product);


        OrderItemRequest orderItemRequest = new OrderItemRequest(product.getId(), quantity);
        List<OrderItemRequest> orderItemRequests = new ArrayList<>(Collections.singletonList(orderItemRequest));
        OrderRequest orderRequest = new OrderRequest(orderItemRequests);

        OrderDto orderDto = orderService.makeOrder(user.getId(), orderRequest);

        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getItems()).hasSize(1);

        int subTotal = productPrice * quantity;
        int otherDiscount = 10 * 100;
        int percentageDiscount = 0;
        int orderTotal = subTotal - otherDiscount - percentageDiscount;

        makeOrderAssertions(orderDto, subTotal, otherDiscount,percentageDiscount, orderTotal);

        ProductCategory secondProductCategory = new ProductCategory();
        secondProductCategory.setCode("new_code");
        secondProductCategory.setName("new_name");
        productCategoryRepository.save(secondProductCategory);
        Product secondProduct = products.get(1);
        secondProduct.setCategory(secondProductCategory);
        secondProduct = productRepository.save(secondProduct);
        orderItemRequests.add(new OrderItemRequest(secondProduct.getId(), quantity));
        orderDto = orderService.makeOrder(user.getId(), orderRequest);
        assertThat(orderDto.getItems()).hasSize(2);

        subTotal = productPrice * quantity + secondProduct.getPrice() * quantity;
        otherDiscount = orderService.getOtherDiscountToApply(subTotal);
        percentageDiscount = (int) (AFFILIATE_DISCOUNT * secondProduct.getPrice() * quantity);
        orderTotal = subTotal - otherDiscount - percentageDiscount;

        makeOrderAssertions(orderDto, subTotal, otherDiscount,percentageDiscount, orderTotal);

    }

    private void makeOrderAssertions(OrderDto orderDto, int subTotal, int otherDiscount,int percentageDiscount, int orderTotal) {
        assertThat(orderDto.getSubTotal()).isEqualTo(Converters.convertCentToActual(subTotal));
        assertThat(orderDto.getOtherDiscount()).isEqualTo(Converters.convertCentToActual(otherDiscount));
        assertThat(orderDto.getPercentageDiscount()).isEqualTo(Converters.convertCentToActual(percentageDiscount));
        assertThat(orderDto.getOrderTotal()).isEqualTo(Converters.convertCentToActual(orderTotal));
        assertThat(orderDto.getOrderStatus()).isEqualTo(OrderStatus.NEW);
    }

}
