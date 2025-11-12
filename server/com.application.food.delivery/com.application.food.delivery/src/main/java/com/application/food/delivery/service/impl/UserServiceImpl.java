package com.application.food.delivery.service.impl;

import com.application.food.delivery.config.PasswordConfig;
import com.application.food.delivery.dto.UserEntityDTO;
import com.application.food.delivery.model.UserEntity;
import com.application.food.delivery.repository.UserRepository;
import com.application.food.delivery.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public boolean createUser(UserEntityDTO user){
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userEntity);
        return true;
    }

    public boolean login(UserEntityDTO user) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isEmpty()){
            throw new RuntimeException("User not found");
          //  return false;
        }
        UserEntity userEntity = optionalUser.get();
        boolean passwordMatch = passwordEncoder.matches(user.getPassword(), userEntity.getPassword());
        if(passwordMatch){
            log.info("{} Login successful", userEntity.getName());
            return true;
        }
        else {
            throw new RuntimeException("Password is wrong");
            //System.out.print("Wrong password");
          //  return false;
        }

    }
}
