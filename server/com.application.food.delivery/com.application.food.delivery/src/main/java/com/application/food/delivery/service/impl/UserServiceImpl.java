package com.application.food.delivery.service.impl;

import com.application.food.delivery.dto.UserEntityDTO;
import com.application.food.delivery.exception.InvalidPasswordException;
import com.application.food.delivery.exception.UserNotFoundException;
import com.application.food.delivery.model.UserEntity;
import com.application.food.delivery.repository.UserRepository;
import com.application.food.delivery.service.UserService;
import com.application.food.delivery.util.OtpService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OtpService otpService;

    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public boolean createUser(UserEntityDTO user){
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isPresent()){
            throw new UserNotFoundException("User already exist, Please login");
        }
        String mobileNumber = userEntity.getPhoneNumber();
        String email = userEntity.getEmail();
        if(OtpService.getVerificationStatus().getOrDefault(email,false) && OtpService.getVerificationStatus().getOrDefault(mobileNumber,false)) {
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
}
