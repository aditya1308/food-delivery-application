package com.application.food.delivery.service;

import com.application.food.delivery.dto.LatLngResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LatLngFinderService {

    @Autowired
    private OlaMapService olaMapService;

    public LatLngResponse findLatLng(String address, String city) {

        // STEP 2.1 → Call OlaMapService
        String response =
                olaMapService.autocomplete(address + ", " + city);

        // STEP 2.2 → Extract lat & lng from JSON
        try {
            JsonNode location = new ObjectMapper()
                    .readTree(response)
                    .path("predictions").get(0)
                    .path("geometry")
                    .path("location");

            double latitude = location.path("lat").asDouble();
            double longitude = location.path("lng").asDouble();

            // STEP 2.3 → Return result
            return new LatLngResponse(latitude, longitude);

        } catch (Exception e) {
            throw new RuntimeException("Latitude / Longitude not found");
        }
    }
}

