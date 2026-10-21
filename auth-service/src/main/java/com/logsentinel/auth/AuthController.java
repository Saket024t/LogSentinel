package com.logsentinel.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public String login(@RequestParam(defaultValue = "true") boolean success) {
        if (success) {
            logger.info("User login successful");
            return "Login successful";
        } else {
            logger.warn("Invalid login attempt detected");
            return "Login failed";
        }
    }
}
