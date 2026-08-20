package com.marketmax.dto.admin;

import com.marketmax.model.User;
import lombok.Data;

@Data
public class UserAdminDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Boolean active;
    private String level;
    private String memberSince;
    private Integer salesCount;

    public static UserAdminDTO fromEntity(User user) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setActive(user.getActive());
        dto.setLevel(user.getLevel());
        dto.setMemberSince(user.getMemberSince());
        dto.setSalesCount(user.getSalesCount());
        return dto;
    }
}
