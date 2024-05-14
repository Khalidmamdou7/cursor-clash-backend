package com.cursorclash.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck(){
        return ResponseEntity.ok().body("Server is up and running");
    }

}
