package com.example.identity_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        return "Hello Bobbbbb and Aliceeeee";
    }
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Hello " + name + "! Welcome to Git!";
    }
}
