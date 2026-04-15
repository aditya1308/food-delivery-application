package com.application.food.delivery.dto;

import lombok.Data;

@Data
public class MenuRequestDTO {
    private String menuName;
    private String description;
    private Long price;
    private String category;
    private boolean isAvailable;
    private boolean isNonVeg;
    private String image;  // Base64 encoded image string (optional)
}

