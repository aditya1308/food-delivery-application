package com.application.food.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
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

}
