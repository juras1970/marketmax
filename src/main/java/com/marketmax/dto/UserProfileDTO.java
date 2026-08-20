package com.marketmax.dto;

import com.marketmax.model.User;
import lombok.Data;

@Data
public class UserProfileDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String level;
    private String memberSince;
    private String avatar;
    private Integer couponsCount;
    private Integer coinsCount;
    private Integer salesCount;

    public static UserProfileDTO fromEntity(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setLevel(user.getLevel());
        dto.setMemberSince(user.getMemberSince());
        dto.setAvatar(user.getAvatarUrl());
        dto.setCouponsCount(user.getCouponsCount());
        dto.setCoinsCount(user.getCoinsCount());
        dto.setSalesCount(user.getSalesCount());
        return dto;
    }
}
