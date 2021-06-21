package com.glivion.backend.service.product;

import com.glivion.backend.domain.model.ProductCategory;
import com.glivion.backend.domain.repository.ProductCategoryRepository;
import com.glivion.backend.payload.dto.product.ProductCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    public List<ProductCategoryDto> getCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(this::toProductCategoryDto)
                .collect(Collectors.toList());

    }

    public ProductCategoryDto getCategory(Integer categoryId){
        Optional<ProductCategory> optionalProductCategory = categoryRepository.findById(categoryId);
        if (optionalProductCategory.isEmpty()){
            throw new EntityNotFoundException();
        }
        ProductCategory productCategory = optionalProductCategory.get();
        return toProductCategoryDto(productCategory);
    }

    private ProductCategoryDto toProductCategoryDto(ProductCategory productCategory){
        return ProductCategoryDto.of(productCategory.getId(), productCategory.getName(), productCategory.getCode(), productCategory.getDescription());
    }
}
