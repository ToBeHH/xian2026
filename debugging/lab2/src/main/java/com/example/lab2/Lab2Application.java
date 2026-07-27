package com.example.lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Lab2Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }

    @GetMapping("/discount")
    public double discount(@RequestParam int orderTotal, @RequestParam String tier) {
        double rate = switch (tier) {
            case "GOLD" -> 0.20;
            case "SILVER" -> 0.10;
            default -> 0.0;
        };
        return orderTotal - (orderTotal * rate);
    }
}
