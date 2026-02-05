package com.application.food.delivery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OlaAuthService {

    @Value("${ola.client-id}")
    private String clientId;

    @Value("${ola.client-secret}")
    private String clientSecret;

    @Value("${ola.token-url}")
    private String tokenUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private String accessToken;
    private long expiryTime;

    public synchronized String getToken() {

        // Return cached token if still valid
        if (accessToken != null && System.currentTimeMillis() < expiryTime) {
            return accessToken;
        }

        // Prepare request body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "openid");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        Map<String, Object> responseBody = response.getBody();

        // Debug: log full response
        System.out.println("Ola API Response: " + responseBody);

        // Check for access_token
        Object tokenObj = responseBody.get("access_token");
        if (tokenObj == null) {
            throw new RuntimeException("Ola API did not return access_token: " + responseBody);
        }

        accessToken = tokenObj.toString();

        // Check for expires_in (optional fallback)
        Object expiresObj = responseBody.get("expires_in");
        if (expiresObj != null) {
            int expiresIn = (Integer) expiresObj;
            expiryTime = System.currentTimeMillis() + (expiresIn - 60) * 1000L; // buffer 1 min
        } else {
            // Default to 10 minutes if missing
            expiryTime = System.currentTimeMillis() + 10 * 60 * 1000L;
            System.out.println("Warning: 'expires_in' missing, using default 10 minutes");
        }

        return accessToken;
    }
}
