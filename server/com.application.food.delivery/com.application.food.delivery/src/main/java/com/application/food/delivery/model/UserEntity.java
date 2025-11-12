package com.application.food.delivery.model;

import com.application.food.delivery.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format"
    )
    @NotBlank(message = "Email cannot be blank")
    private String email;
    private String name;
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number. Must be 10 digits and start with 6–9"
    )
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;
    private String password;
    private String createdAt;
    @Enumerated(EnumType.STRING)
    private Role role;
}
