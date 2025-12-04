package com.application.food.delivery.model;

import jakarta.persistence.*;

@Entity
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity user;

    private String addressLine;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private String pincode;

}
