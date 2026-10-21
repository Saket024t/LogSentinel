package com.logsentinel.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public String createOrder() {
        logger.info("Order created successfully");
        return "Order created";
    }

    @GetMapping("/simulate-error")
    public String simulateError() {
        RuntimeException ex = new RuntimeException("Database connection failed during order simulation");
        logger.error("Failed to process order simulation", ex);
        throw ex;
    }
}
