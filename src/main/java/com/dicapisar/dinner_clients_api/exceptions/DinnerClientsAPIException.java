package com.dicapisar.dinner_clients_api.exceptions;

import com.dicapisar.dinner_clients_api.dtos.ErrorDTO;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class DinnerClientsAPIException extends Exception {
    private final ErrorDTO error;
    private final HttpStatus status;

    public DinnerClientsAPIException(String message, HttpStatus status) {
        this.error = new ErrorDTO();
        this.error.setMessage(message);
        this.error.setErrorName(this.getClass().getSimpleName());
        this.status = status;
    }
}
