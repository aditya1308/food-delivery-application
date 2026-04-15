package com.application.food.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "menu_entity")
@Getter
@Setter
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;
    private String menuName;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private RestaurantEntity restaurant;

    private String description;

    @Column(name = "image", columnDefinition = "BYTEA", nullable = true)
    private byte[] image;
    private Long price;
    private boolean isAvailable;
    private String category;
    private boolean isNonVeg;
}
