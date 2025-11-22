
package com.example.demo.controller;

import.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello() {
        return "Hello World from Spring Boot!";
    }
}
