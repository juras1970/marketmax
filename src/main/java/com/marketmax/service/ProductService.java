package com.marketmax.service;

import com.marketmax.dto.ProductDTO;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.Product;
import com.marketmax.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDTO> findAll(String query, String category) {
        List<Product> products;

        boolean hasQuery = StringUtils.hasText(query);
        boolean hasCategory = StringUtils.hasText(category);

        if (hasQuery && hasCategory) {
            products = productRepository.searchByQueryAndCategory(query, category);
        } else if (hasQuery) {
            products = productRepository.searchByTitleOrDescription(query);
        } else if (hasCategory) {
            products = productRepository.findByCategory(category);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public List<ProductDTO> findPromotions() {
        return productRepository.findByDiscountGreaterThanEqualOrderByDiscountDesc(1).stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public ProductDTO findById(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
        return ProductDTO.fromEntity(product);
    }
}
