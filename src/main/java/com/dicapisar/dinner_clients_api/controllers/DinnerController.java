package com.dicapisar.dinner_clients_api.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/dinner")
public class DinnerController {

    @PostMapping("/generate")
    public ResponseEntity<String> generateDinner(@RequestBody String orderDinner) {
        return new ResponseEntity<>("Generating Dinner", HttpStatus.OK);
    }
}
