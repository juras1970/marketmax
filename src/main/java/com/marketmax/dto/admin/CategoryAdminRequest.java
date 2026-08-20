package com.marketmax.dto.admin;

import lombok.Data;

@Data
public class CategoryAdminRequest {
    private String id;
    private String name;
    private String icon;
    private Integer displayOrder;
}
