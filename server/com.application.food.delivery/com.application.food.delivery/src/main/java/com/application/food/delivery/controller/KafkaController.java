package com.application.food.delivery.controller;

import com.application.food.delivery.kafka.KafkaProducerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TEST CONTROLLER - Only for testing kafka configs - working fine
@RestController
public class KafkaController {

    private final KafkaProducerService producerService;

    public KafkaController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @GetMapping("/publish")
    public String publish(@RequestParam String msg) {
        producerService.send(msg);
        return "✅ Message sent to Kafka: " + msg;
    }
}
