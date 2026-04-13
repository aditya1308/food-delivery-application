package com.application.food.delivery.service.impl;

import com.application.food.delivery.dto.LatLngResponse;
import com.application.food.delivery.model.RestaurantEntity;
import com.application.food.delivery.model.UserEntity;
import com.application.food.delivery.repository.RestaurantRepository;
import com.application.food.delivery.repository.UserRepository;
import com.application.food.delivery.service.LatLngFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LatLngFinderService latLngFinderService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public void createRestaurant(Long id, RestaurantEntity restaurant) {

        LatLngResponse latLng =
                latLngFinderService.findLatLng(
                        restaurant.getAddress(),
                        restaurant.getCity()
                );

        UserEntity userEntity = userRepository.findById(id).orElse(null);
        restaurant.setLatitude(latLng.getLatitude());
        restaurant.setLongitude(latLng.getLongitude());
        restaurant.setOwnerId(userEntity);
        restaurantRepository.save(restaurant);
    }

}
