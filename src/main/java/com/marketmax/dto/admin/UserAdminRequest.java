package com.marketmax.dto.admin;

import lombok.Data;

@Data
public class UserAdminRequest {
    private String name;
    private String email;
    private String password;
    private String role;
    private Boolean active;
    private String level;
}
