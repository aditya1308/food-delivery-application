package com.application.food.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private UserEntity ownerId;

    private String name;
    private String address;
    private String description;
    private Double latitude;
    private Double longitude;
    private String city;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MenuEntity> menus = new ArrayList<>();
}
