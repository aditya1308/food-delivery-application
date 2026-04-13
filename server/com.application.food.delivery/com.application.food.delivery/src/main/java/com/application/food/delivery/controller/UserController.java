package com.application.food.delivery.controller;

import com.application.food.delivery.dto.UserEntityDTO;
import com.application.food.delivery.model.AddressEntity;
import com.application.food.delivery.model.RestaurantEntity;
import com.application.food.delivery.service.JwtService;
import com.application.food.delivery.service.OlaMapService;
import com.application.food.delivery.service.impl.UserServiceImpl;
import com.application.food.delivery.util.OtpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserServiceImpl userServiceImpl;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OlaMapService olaMapsService;

    public UserController(UserServiceImpl userServiceImpl, OtpService otpService,
                         AuthenticationManager authenticationManager, JwtService jwtService,
                         OlaMapService olaMapsService) {
        this.userServiceImpl = userServiceImpl;
        this.otpService = otpService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.olaMapsService = olaMapsService;
    }


    @PostMapping("/login")
     public ResponseEntity<?> login(@RequestBody UserEntityDTO user) {
         userServiceImpl.login(user);
      return ResponseEntity.ok().body("Login successful");
     }

    @PostMapping("/loginAuth")
    public ResponseEntity<?> loginAuth(@RequestBody UserEntityDTO user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
            String token = jwtService.generateToken(
                    authentication.getName(),
                    "USER",  // or fetch role from DB
                    user.getEmail()
            );

            return ResponseEntity.ok(token);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

     @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserEntityDTO user) {
         boolean isUSerCreated = userServiceImpl.createUser(user);
         if(isUSerCreated){
             return ResponseEntity.ok().body("User created successfully");
         }
         return new ResponseEntity<>("User not verified,Please Verify", HttpStatus.UNAUTHORIZED);
     }

    @PostMapping("/sendtomail")
    public ResponseEntity<String> sendEmailOtp(@RequestBody Map<String, String> req) {
        if (!req.containsKey("email") || req.get("email").isBlank()) {
            return ResponseEntity.badRequest().body("❌ Email is required");
        }

        String result = otpService.sendOtpToEmail(req.get("email"));

        if (result.contains("success")) {
            return ResponseEntity.ok("📧 OTP sent to email successfully");
        }

        return ResponseEntity.internalServerError().body("❌ Failed to send OTP to email");
    }

    @PostMapping("/sendtonumber")
    public ResponseEntity<String> sendMobileOtp(@RequestBody Map<String, String> req) {
        if (!req.containsKey("mobile") || req.get("mobile").isBlank()) {
            return ResponseEntity.badRequest().body("❌ Mobile number is required");
        }

        String result = otpService.sendOtpToMobile(req.get("mobile"));

        if (result.contains("generated")) {
            return ResponseEntity.ok("📱 OTP sent to mobile successfully");
        }

        return ResponseEntity.internalServerError().body("❌ Failed to send OTP to mobile");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> req) {

        boolean isValid = otpService.verifyOtp(req);
        if (isValid)
            return ResponseEntity.ok("✅ OTP verified successfully!");
        else
            return ResponseEntity.badRequest().body("❌ Invalid or expired OTP.");
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> req) {
        boolean isPasswordReset = userServiceImpl.resetPassword(req);
        return isPasswordReset ? ResponseEntity.ok().body("Password rest successful") : ResponseEntity.badRequest().body("Try again later!");
    }

    @GetMapping("/get-details")
    public ResponseEntity<?> getUserDetails() {
         return ResponseEntity.ok().body("Successful!");
    }


    // MAP related APIs
    @PostMapping("/add-address/{id}")
    public ResponseEntity<?>createRestaurant(@PathVariable Long id, @RequestBody AddressEntity address ) {
        userServiceImpl.createRestaurant(id, address);
        return ResponseEntity.ok().body("Address added successfully!");

    }

    @GetMapping("/address/search")
    public ResponseEntity<String> searchAddress(@RequestParam String query) {
        return ResponseEntity.ok(olaMapsService.autocomplete(query));
    }

    @GetMapping("/get-restaurants/{id}")
    public ResponseEntity<?> getRestaurants(@PathVariable Long id) {
        List<RestaurantEntity> serviceableRestaurants = userServiceImpl.getRestaurants(id);
        return ResponseEntity.ok(serviceableRestaurants);
    }
}


