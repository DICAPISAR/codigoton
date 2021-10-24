package com.dicapisar.dinner_clients_api.controllers;

import com.dicapisar.dinner_clients_api.exceptions.DinnerClientsAPIException;
import com.dicapisar.dinner_clients_api.services.IDinnerService;
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

    private IDinnerService dinnerService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateDinner(@RequestBody String orderDinner) throws DinnerClientsAPIException {
        return new ResponseEntity<>(dinnerService.generateDinner(orderDinner), HttpStatus.OK);
    }
}
