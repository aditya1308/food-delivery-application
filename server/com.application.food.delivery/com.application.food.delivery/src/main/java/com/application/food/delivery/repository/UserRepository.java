package com.application.food.delivery.repository;

import com.application.food.delivery.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
       Optional<UserEntity> findByEmail(String email);
       Optional<UserEntity>findByPhoneNumber(String phoneNumber);
       Optional<UserEntity>findByPassword(String password);
}
