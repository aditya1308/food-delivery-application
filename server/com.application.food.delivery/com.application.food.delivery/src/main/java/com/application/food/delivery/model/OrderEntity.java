//package com.application.food.delivery.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Getter
//@Setter
//public class OrderEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long orderId;
//
//    @ManyToOne
//    @JoinColumn(name = "restaurantId")
//    private RestaurantEntity restaurant;
//
//    @ManyToOne
//    @JoinColumn(name = "menuId")
//    private MenuEntity menu;
//
//    @ManyToOne
//    @JoinColumn(name = "addressId")
//    private AddressEntity address;
//
//    @ManyToOne
//    @JoinColumn(name = "userId")
//    private UserEntity user;
//
//    @ManyToOne
//    @JoinColumn(name = "deliveryUserId")
//    private UserEntity delivery;
//
//    private String paymentStatus;
//    private Double price;
//    private LocalDateTime createdAt;
//    private String orderStatus;
//}
