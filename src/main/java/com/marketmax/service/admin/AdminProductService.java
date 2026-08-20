package com.marketmax.service.admin;

import com.marketmax.dto.ProductDTO;
import com.marketmax.dto.admin.ProductAdminRequest;
import com.marketmax.exception.BusinessException;
import com.marketmax.exception.ResourceNotFoundException;
import com.marketmax.model.Product;
import com.marketmax.model.ProductImage;
import com.marketmax.model.ProductSpec;
import com.marketmax.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
            .map(ProductDTO::fromEntity)
            .collect(Collectors.toList());
    }

    public ProductDTO create(ProductAdminRequest request) {
        validate(request);

        String id = StringUtils.hasText(request.getId()) ? request.getId() : generateId(request.getTitle());
        if (productRepository.existsById(id)) {
            throw new BusinessException("Já existe um produto com o id: " + id);
        }

        Product product = Product.builder().id(id).build();
        applyFields(product, request);

        productRepository.save(product);
        return ProductDTO.fromEntity(product);
    }

    public ProductDTO update(String id, ProductAdminRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));

        validate(request);
        applyFields(product, request);

        productRepository.save(product);
        return ProductDTO.fromEntity(product);
    }

    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com id: " + id);
        }
        productRepository.deleteById(id);
    }

    private void applyFields(Product product, ProductAdminRequest request) {
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setDiscount(request.getDiscount() != null ? request.getDiscount() : 0);
        product.setCategory(request.getCategory());
        product.setFreeShipping(Boolean.TRUE.equals(request.getFreeShipping()));
        product.setDescription(request.getDescription());
        product.setRating(request.getRating());
        product.setSalesCountText(request.getSalesCountText());
        product.setIsNew(Boolean.TRUE.equals(request.getIsNew()));
        product.setSellerReputation(request.getSellerReputation());
        product.setSellerRating(request.getSellerRating());
        product.setSellerSales(request.getSellerSales());
        product.setSellerPosting(request.getSellerPosting());

        product.getImages().clear();
        if (request.getImages() != null) {
            int order = 0;
            for (String url : request.getImages()) {
                if (!StringUtils.hasText(url)) continue;
                product.getImages().add(ProductImage.builder()
                    .product(product)
                    .imageUrl(url.trim())
                    .displayOrder(order++)
                    .build());
            }
        }

        product.getSpecs().clear();
        if (request.getSpecs() != null) {
            for (Map.Entry<String, String> entry : request.getSpecs().entrySet()) {
                if (!StringUtils.hasText(entry.getKey())) continue;
                product.getSpecs().add(ProductSpec.builder()
                    .product(product)
                    .specKey(entry.getKey())
                    .specValue(entry.getValue())
                    .build());
            }
        }
    }

    private void validate(ProductAdminRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new BusinessException("O título do produto é obrigatório");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O preço do produto é obrigatório e deve ser positivo");
        }
        if (!StringUtils.hasText(request.getCategory())) {
            throw new BusinessException("A categoria do produto é obrigatória");
        }
    }

    private String generateId(String title) {
        String base = Normalizer.normalize(title == null ? "produto" : title, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase()
            .trim();
        base = Pattern.compile("[^a-z0-9]+").matcher(base).replaceAll("-");
        base = base.replaceAll("(^-|-$)", "");
        if (base.length() > 40) base = base.substring(0, 40);
        if (!StringUtils.hasText(base)) base = "produto";
        return base + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
