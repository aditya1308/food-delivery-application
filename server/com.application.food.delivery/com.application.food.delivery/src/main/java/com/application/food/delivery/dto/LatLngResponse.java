package com.application.food.delivery.dto;


import lombok.Data;

@Data
public class LatLngResponse {
    private double latitude;
    private double longitude;

    public LatLngResponse(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
