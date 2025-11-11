package com.application.food.delivery.service;

import com.application.food.delivery.dto.UserEntityDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    public boolean createUser(UserEntityDTO user);
    public boolean login(UserEntityDTO user);
}
