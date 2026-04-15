package com.application.food.delivery.service.impl;

import com.application.food.delivery.dto.LatLngResponse;
import com.application.food.delivery.dto.MenuRequestDTO;
import com.application.food.delivery.model.MenuEntity;
import com.application.food.delivery.model.RestaurantEntity;
import com.application.food.delivery.model.UserEntity;
import com.application.food.delivery.repository.RestaurantRepository;
import com.application.food.delivery.repository.UserRepository;
import com.application.food.delivery.service.LatLngFinderService;
import com.application.food.delivery.service.RestaurantService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final UserRepository userRepository;
    private final LatLngFinderService latLngFinderService;
    private final RestaurantRepository restaurantRepository;

    public RestaurantServiceImpl(UserRepository userRepository, LatLngFinderService latLngFinderService, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.latLngFinderService = latLngFinderService;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public void addMenu(Long id, MenuRequestDTO menuDTO) {
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setMenuName(menuDTO.getMenuName());
        menuEntity.setDescription(menuDTO.getDescription());
        menuEntity.setPrice(menuDTO.getPrice());
        menuEntity.setCategory(menuDTO.getCategory());
        menuEntity.setAvailable(menuDTO.isAvailable());
        menuEntity.setNonVeg(menuDTO.isNonVeg());
        menuEntity.setRestaurant(restaurantEntity);

        // Convert base64 string to byte array if image is provided
        if (menuDTO.getImage() != null && !menuDTO.getImage().isEmpty()) {
            String base64String = menuDTO.getImage();
            if (base64String.contains(",")) {
                base64String = base64String.split(",")[1];
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            menuEntity.setImage(imageBytes);
        }
        
        restaurantEntity.getMenus().add(menuEntity);
        restaurantRepository.save(restaurantEntity);
    }

    public void createRestaurant(Long id, RestaurantEntity restaurant) {

        LatLngResponse latLng = latLngFinderService.findLatLng(restaurant.getAddress(), restaurant.getCity());

        UserEntity userEntity = userRepository.findById(id).orElse(null);
        restaurant.setLatitude(latLng.getLatitude());
        restaurant.setLongitude(latLng.getLongitude());
        restaurant.setOwnerId(userEntity);
        restaurantRepository.save(restaurant);
    }

    public List<MenuEntity> getMenu(Long id) {
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        return restaurantEntity.getMenus();
    }

}
