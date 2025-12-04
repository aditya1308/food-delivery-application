package com.application.food.delivery.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private UserEntity owner;

    private String name;
    private String location;
    private String address;
    private String description;
    private String city;
    private String isActive;
    private LocalDateTime createdAt;
}
