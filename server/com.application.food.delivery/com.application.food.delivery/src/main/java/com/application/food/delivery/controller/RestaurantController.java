package com.application.food.delivery.controller;

import com.application.food.delivery.dto.MenuRequestDTO;
import com.application.food.delivery.model.RestaurantEntity;
import com.application.food.delivery.service.impl.RestaurantServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantServiceImpl restaurantServiceImpl;

    public RestaurantController(RestaurantServiceImpl restaurantServiceImpl) {
        this.restaurantServiceImpl = restaurantServiceImpl;
    }
    
    @PostMapping("{id}/add-menu")
    public ResponseEntity<?> addMenu(@PathVariable Long id, @RequestBody MenuRequestDTO menuDTO) {
        restaurantServiceImpl.addMenu(id, menuDTO);
        return ResponseEntity.ok().body("Menu added successfully!");
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createRestaurant(@PathVariable Long id, @RequestBody RestaurantEntity restaurantEntity) {
        restaurantServiceImpl.createRestaurant(id, restaurantEntity);
        return ResponseEntity.ok().body("Restaurant created successfully!");
    }

    @GetMapping("{id}/menu")
    public ResponseEntity<?> getMenu(@PathVariable Long id) {
        return ResponseEntity.ok().body(restaurantServiceImpl.getMenu(id));
    }

}
