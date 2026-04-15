package com.application.food.delivery.service;

import com.application.food.delivery.dto.MenuRequestDTO;
import com.application.food.delivery.model.RestaurantEntity;

public interface RestaurantService {
    void createRestaurant(Long id, RestaurantEntity restaurant);
    void addMenu(Long id, MenuRequestDTO menuDTO);
}
