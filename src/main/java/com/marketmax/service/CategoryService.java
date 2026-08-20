package com.marketmax.service;

import com.marketmax.dto.CategoryDTO;
import com.marketmax.model.Category;
import com.marketmax.repository.CategoryRepository;
import com.marketmax.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryDTO> findAll() {
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : productRepository.countProductsByCategory()) {
            counts.put((String) row[0], (Long) row[1]);
        }

        return categoryRepository.findAll().stream()
            .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getIcon(), counts.getOrDefault(c.getId(), 0L)))
            .sorted((a, b) -> b.getProductCount().compareTo(a.getProductCount()))
            .collect(Collectors.toList());
    }
}
