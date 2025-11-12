package com.application.food.delivery.controller;

import com.application.food.delivery.dto.UserEntityDTO;
import com.application.food.delivery.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;

     @PostMapping("/login")
     public ResponseEntity<?> login(@RequestBody UserEntityDTO user) {
         userServiceImpl.login(user);
      return ResponseEntity.ok().body("Login successful");
     }

     @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserEntityDTO user) {
         userServiceImpl.createUser(user);
         return ResponseEntity.ok().body("Signup successful");
     }

}
