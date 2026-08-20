package com.marketmax.dto.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductAdminRequest {
    private String id;
    private String title;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discount;
    private String category;
    private Boolean freeShipping;
    private String description;
    private BigDecimal rating;
    private String salesCountText;
    private Boolean isNew;
    private String sellerReputation;
    private String sellerRating;
    private String sellerSales;
    private String sellerPosting;
    private List<String> images;
    private Map<String, String> specs;
}
