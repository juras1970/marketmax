package com.marketmax.service.admin;

import com.marketmax.dto.CategoryDTO;
import com.marketmax.dto.admin.CategoryAdminRequest;
import com.marketmax.exception.BusinessException;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.Category;
import com.marketmax.repository.CategoryRepository;
import com.marketmax.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : productRepository.countProductsByCategory()) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return categoryRepository.findAll().stream()
            .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getIcon(), counts.getOrDefault(c.getId(), 0L)))
            .collect(Collectors.toList());
    }

    public CategoryDTO create(CategoryAdminRequest request) {
        if (!StringUtils.hasText(request.getId()) || !StringUtils.hasText(request.getName())) {
            throw new BusinessException("O id e o nome da categoria são obrigatórios");
        }
        String id = request.getId().trim().toLowerCase().replaceAll("[^a-z0-9-]", "");
        if (categoryRepository.existsById(id)) {
            throw new BusinessException("Já existe uma categoria com este id");
        }
        Category category = Category.builder()
            .id(id)
            .name(request.getName().trim())
            .icon(StringUtils.hasText(request.getIcon()) ? request.getIcon() : "🛍️")
            .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
            .build();
        categoryRepository.save(category);
        return new CategoryDTO(category.getId(), category.getName(), category.getIcon(), 0L);
    }

    public CategoryDTO update(String id, CategoryAdminRequest request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + id));

        if (StringUtils.hasText(request.getName())) category.setName(request.getName().trim());
        if (StringUtils.hasText(request.getIcon())) category.setIcon(request.getIcon());
        if (request.getDisplayOrder() != null) category.setDisplayOrder(request.getDisplayOrder());

        categoryRepository.save(category);

        long count = productRepository.findByCategory(id).size();
        return new CategoryDTO(category.getId(), category.getName(), category.getIcon(), count);
    }

    public void delete(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada com id: " + id);
        }
        long productsInCategory = productRepository.findByCategory(id).size();
        if (productsInCategory > 0) {
            throw new BusinessException("Não é possível excluir: existem " + productsInCategory + " produto(s) nesta categoria");
        }
        categoryRepository.deleteById(id);
    }
}
