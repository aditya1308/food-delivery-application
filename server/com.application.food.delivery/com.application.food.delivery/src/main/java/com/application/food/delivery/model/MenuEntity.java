package com.application.food.delivery.model;

import jakarta.persistence.*;

@Entity
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @ManyToOne
    @JoinColumn(name = "restaurantId")
    private RestaurantEntity restaurant;

    private String description;
    private String image;
    private String price;
    private boolean isAvailable;
    private String category;
}
