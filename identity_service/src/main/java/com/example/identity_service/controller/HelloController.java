package com.example.identity_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
<<<<<<< HEAD
        return "Hello from Alice to Bob";
=======
        return "Hello from Alice & Bob"
>>>>>>> origin/main
    }
}
