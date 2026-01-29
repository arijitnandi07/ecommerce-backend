package com.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/api")
    public String home() {
        return "E-Commerce API is running. Use /products, /cart, /orders, /users";
    }

    @GetMapping("/api/v1")
    public String homeV1() {
        return "E-Commerce API v1 is running";
    }
}
