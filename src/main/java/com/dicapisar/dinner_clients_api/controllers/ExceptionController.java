package com.dicapisar.dinner_clients_api.controllers;

import com.dicapisar.dinner_clients_api.dtos.ErrorDTO;
import com.dicapisar.dinner_clients_api.exceptions.DinnerClientsAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(DinnerClientsAPIException.class)
    public ResponseEntity<ErrorDTO> handleGlobalException (DinnerClientsAPIException e) {
        return new ResponseEntity<>(e.getError(), e.getStatus());
    }
}
