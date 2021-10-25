package com.dicapisar.dinner_clients_api.controllers;

import com.dicapisar.dinner_clients_api.dtos.ErrorDTO;
import com.dicapisar.dinner_clients_api.exceptions.DinnerClientsAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    /**
     * Method that is in charge of controlling all the exceptions that are in the execution of the API
     * @param e DinnerClientsAPIException
     * @return Respective error with its respective code
     */
    @ExceptionHandler(DinnerClientsAPIException.class)
    public ResponseEntity<ErrorDTO> handleGlobalException (DinnerClientsAPIException e) {
        return new ResponseEntity<>(e.getError(), e.getStatus());
    }
}
