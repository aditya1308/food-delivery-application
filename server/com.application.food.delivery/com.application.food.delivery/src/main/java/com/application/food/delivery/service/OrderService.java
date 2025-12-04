package com.application.food.delivery.service;

import com.application.food.delivery.model.OrderEntity;

public interface OrderService {
    public boolean createOrder(OrderEntity order);
}
