package com.application.food.delivery.repository;

import com.application.food.delivery.model.AuthorizedAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorizedAccessRepository extends JpaRepository<AuthorizedAccess, Long> {
    Optional<AuthorizedAccess> findByEmail(String email);
}
