package com.payroutex.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "PayRouteX Backend is running successfully. Use /api/gateways to test APIs.";
    }
}