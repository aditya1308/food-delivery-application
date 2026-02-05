package com.application.food.delivery.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OlaMapService {

    @Value("${ola.base-url}")
    private String baseUrl;

    @Autowired
    private OlaAuthService olaAuthService;

    private final RestTemplate restTemplate = new RestTemplate();

    public String autocomplete(String input) {

        String token = olaAuthService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        String url = baseUrl + "/places/v1/autocomplete?input=" + input;

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}

