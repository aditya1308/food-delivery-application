package com.application.food.delivery.service.impl;

import com.application.food.delivery.dto.LatLngResponse;
import com.application.food.delivery.dto.UserEntityDTO;
import com.application.food.delivery.enums.Role;
import com.application.food.delivery.exception.InvalidPasswordException;
import com.application.food.delivery.exception.UserNotFoundException;
import com.application.food.delivery.model.AddressEntity;
import com.application.food.delivery.model.RestaurantEntity;
import com.application.food.delivery.model.UserEntity;
import com.application.food.delivery.repository.AddressRepository;
import com.application.food.delivery.repository.RestaurantRepository;
import com.application.food.delivery.repository.UserRepository;
import com.application.food.delivery.service.LatLngFinderService;
import com.application.food.delivery.service.UserService;
import com.application.food.delivery.util.DistanceCalculator;
import com.application.food.delivery.util.OtpService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final LatLngFinderService latLngFinderService;
    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;

    private final double DISTANCE_RANGE = 5.0;

    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder, OtpService otpService,
                           LatLngFinderService latLngFinderService, AddressRepository addressRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.latLngFinderService = latLngFinderService;
        this.addressRepository = addressRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public boolean createUser(UserEntityDTO user){
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isPresent()){
            throw new UserNotFoundException("User already exist, Please login");
        }
        String mobileNumber = userEntity.getPhoneNumber();
        String email = userEntity.getEmail();
        if(OtpService.getVerificationStatus().getOrDefault(email,true) && OtpService.getVerificationStatus().getOrDefault(mobileNumber,true)) {
            log.info("User verified");
            userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(userEntity);
            OtpService.getVerificationStatus().remove(email);
            OtpService.getVerificationStatus().remove(mobileNumber);
            return true;
        }
        log.info("User not verified");
        return false;
    }

    public boolean login(UserEntityDTO user) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isEmpty()){
            throw new UserNotFoundException("User does not exist, Please register");
        }
        UserEntity userEntity = optionalUser.get();
        boolean passwordMatch = passwordEncoder.matches(user.getPassword(), userEntity.getPassword());
        if(passwordMatch){
            log.info("{} Login successful", userEntity.getName());
            return true;
        }
        else {
            throw new InvalidPasswordException("Invalid password");
        }

    }

    public boolean resetPassword(Map<String, String> req) {
        log.info("In reset Password");
        Optional<UserEntity> optionalUser = Optional.empty();
        if (req.containsKey("email")) {
            // User selected email
            if(OtpService.getVerificationStatus().getOrDefault(req.get("email"),false)) {
                optionalUser = userRepository.findByEmail(req.get("email"));
                if (optionalUser.isEmpty()) {
                    throw new RuntimeException("No user found with this email");
                }
                optionalUser.get().setPassword(passwordEncoder.encode(req.get("password")));
                userRepository.save(optionalUser.get());
                return true;
            }
        }
        else if (req.containsKey("mobileNo")) {
            // User selected phone number
            if(OtpService.getVerificationStatus().getOrDefault(req.get("mobileNo"),false)) {
                optionalUser = userRepository.findByPhoneNumber(req.get("mobileNo"));
                if (optionalUser.isEmpty()) {
                    throw new RuntimeException("No user found with this phone number");
                }
                UserEntity savedUser = optionalUser.get();
                savedUser.setPassword(passwordEncoder.encode(req.get("password")));
                userRepository.save(savedUser);
                return true;
            }
        }
        else {
            throw new RuntimeException("Email or phone number must be provided");
        }
        return false;
    }

    @Transactional
    public void createRestaurant(Long id, AddressEntity address) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()){
            throw new UserNotFoundException("User does not exist, Please register!");
        }

        UserEntity userEntity = optionalUser.get();
        boolean isUser = userEntity.getRole().equals(Role.USER);

        if(isUser) {
            AddressEntity addressEntity = new AddressEntity();
            addressEntity.setAddressLine(address.getAddressLine());
            LatLngResponse latLngResponse = latLngFinderService.findLatLng(address.getAddressLine(), address.getCity());
            addressEntity.setLatitude(latLngResponse.getLatitude());
            addressEntity.setLongitude(latLngResponse.getLongitude());
            addressEntity.setCity(address.getCity());
            addressEntity.setState(address.getState());
            addressEntity.setPincode(address.getPincode());
            addressEntity.setUser(userEntity);
            
            AddressEntity savedAddress = addressRepository.save(addressEntity);
            log.info("Address saved successfully with ID: {} for user: {}", savedAddress.getAddressId(), userEntity.getName());
        } else {
            throw new RuntimeException("Please register as a user to add address");
        }
    }

    public List<RestaurantEntity> getRestaurants(Long id) {

        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist, Please register!");
        }
        UserEntity userEntity = optionalUser.get();

        List<RestaurantEntity> restaurants = restaurantRepository.findAll();
        List<AddressEntity> addressEntityList = userEntity.getAddresses();
        List<RestaurantEntity> serviceableRestaurants = new ArrayList<>();
        for (AddressEntity addressEntity : addressEntityList) {
            Double userLatitude = addressEntity.getLatitude();
            Double userLongitude = addressEntity.getLongitude();
            for (RestaurantEntity restaurantEntity : restaurants) {
                Double restaurantLatitude = restaurantEntity.getLatitude();
                Double restaurantLongitude = restaurantEntity.getLongitude();
                double distance = DistanceCalculator.calculateDistance(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude);
                if (distance <= DISTANCE_RANGE) {
                    log.info("Distance from user {} to restaurant {} is {} k.m.", userEntity.getName(), restaurantEntity.getName(), distance);
                    serviceableRestaurants.add(restaurantEntity);
                }
            }
        }
        log.info("Number of restaurants found: {}", serviceableRestaurants.size());
        return serviceableRestaurants;
    }
}
