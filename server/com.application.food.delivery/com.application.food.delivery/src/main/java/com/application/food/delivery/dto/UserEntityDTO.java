package com.application.food.delivery.dto;
import com.application.food.delivery.enums.Role;
import lombok.Data;

@Data
public class UserEntityDTO {
    private long id;
    private String phoneNumber;
    private String password;
    private String createdAt;
    private String email;
    private Role role;
}
