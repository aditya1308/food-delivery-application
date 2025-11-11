package com.application.food.delivery.service.impl;

import com.application.food.delivery.config.PasswordConfig;
import com.application.food.delivery.dto.UserEntityDTO;
import com.application.food.delivery.model.UserEntity;
import com.application.food.delivery.repository.UserRepository;
import com.application.food.delivery.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public boolean createUser(UserEntityDTO user){
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userEntity);
        return true;
    }

    public boolean login(UserEntityDTO user){
        Optional<UserEntity> userEntityWithEmail = userRepository.findByEmail(user.getEmail());
        Optional<UserEntity> userEntityWithNumber = userRepository.findByPhoneNumber(user.getPhoneNumber());
     if(userEntityWithEmail.isPresent() || userEntityWithNumber.isPresent()) {
         UserEntity userEntity = userEntityWithEmail.get();
         return passwordEncoder.matches(user.getPassword(), userEntity.getPassword());
     }
     return false;
    }
}
