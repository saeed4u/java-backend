package com.glivion.backend.service.product;

import com.glivion.backend.domain.model.ProductStock;
import com.glivion.backend.domain.repository.ProductStockRepository;
import com.glivion.backend.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@AllArgsConstructor

//a very minimal implementation of products stocking
public class ProductStockService {

    private final ProductStockRepository repository;

    public void deductQuantity(int stockId, int quantity){
        Optional<ProductStock> optionalProductStock = repository.findById(stockId);
        if (optionalProductStock.isEmpty()){
            throw new EntityNotFoundException();
        }
        ProductStock productStock = optionalProductStock.get();
        Integer availableQuantity = productStock.getAvailableQuantity();

        if (quantity > availableQuantity){
            throw new BadRequestException("Quantity requested is greater than available quantity");
        }

        Integer quantityOnHold = productStock.getQuantityOnHold();
        productStock.setQuantityOnHold(quantityOnHold + quantity);
        productStock.setAvailableQuantity(availableQuantity - quantity);
        repository.save(productStock);
    }

    public void confirmQuantity(int stockId, int quantity){
        Optional<ProductStock> optionalProductStock = repository.findById(stockId);
        if (optionalProductStock.isEmpty()){
            throw new EntityNotFoundException();
        }
        ProductStock productStock = optionalProductStock.get();
        Integer quantityOnHold = productStock.getQuantityOnHold();

        if (quantity > quantityOnHold){
            throw new BadRequestException("Quantity requested is greater than quantity on hold");
        }
        productStock.setQuantityOnHold(quantityOnHold - quantity);
        productStock.setTotalQuantity(productStock.getTotalQuantity() - quantity);
        repository.save(productStock);
    }

    public void returnQuantity(int stockId, int quantity){
        Optional<ProductStock> optionalProductStock = repository.findById(stockId);
        if (optionalProductStock.isEmpty()){
            throw new EntityNotFoundException();
        }
        ProductStock productStock = optionalProductStock.get();
        Integer quantityOnHold = productStock.getQuantityOnHold();
        Integer availableQuantity = productStock.getAvailableQuantity();

        if (quantity > quantityOnHold){
            throw new BadRequestException("Quantity requested is greater than quantity on hold");
        }
        productStock.setQuantityOnHold(quantityOnHold - quantity);
        productStock.setQuantityOnHold(availableQuantity + quantity);
        repository.save(productStock);
    }

}
