package com.marketmax.dto;

import com.marketmax.model.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ProductDTO {

    private String id;
    private String title;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discount;
    private String category;
    private Boolean freeShipping;
    private String description;
    private BigDecimal rating;
    private String salesCount;
    private Boolean isNew;
    private String sellerReputation;
    private String sellerRating;
    private String sellerSales;
    private String sellerPosting;
    private List<String> images;
    private Map<String, String> specs;

    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setDiscount(product.getDiscount());
        dto.setCategory(product.getCategory());
        dto.setFreeShipping(product.getFreeShipping());
        dto.setDescription(product.getDescription());
        dto.setRating(product.getRating());
        dto.setSalesCount(product.getSalesCountText());
        dto.setIsNew(product.getIsNew());
        dto.setSellerReputation(product.getSellerReputation());
        dto.setSellerRating(product.getSellerRating());
        dto.setSellerSales(product.getSellerSales());
        dto.setSellerPosting(product.getSellerPosting());

        dto.setImages(product.getImages().stream()
            .map(img -> img.getImageUrl())
            .collect(Collectors.toList()));

        Map<String, String> specsMap = new LinkedHashMap<>();
        product.getSpecs().forEach(spec -> specsMap.put(spec.getSpecKey(), spec.getSpecValue()));
        dto.setSpecs(specsMap);

        return dto;
    }
}
