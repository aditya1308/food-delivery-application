package com.application.food.delivery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())  // (1) Disable CSRF for POST testing
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/login",
                                "/api/v1/signup",
                                "/api/v1/sendtomail",
                                "/api/v1/sendtonumber",
                                "/api/v1/verify",
                                "/api/v1/forgot"
                        ).permitAll()            // (2) Public endpoints
                        .anyRequest().authenticated() // (3) Protect all others
                )
                .httpBasic(basic -> {});  // (4) Enable Basic Auth

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

}
