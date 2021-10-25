package com.dicapisar.dinner_clients_api.exceptions;

import com.dicapisar.dinner_clients_api.dtos.ErrorDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class DinnerClientsAPIException extends Exception {
    private final ErrorDTO error;
    private final HttpStatus status;

    /**
     * DinnerClientsAPIException constructor method
     * @param message String where the error message to be reported in the API is indicated
     * @param status HttpStatus object which indicates the http code of the error to report
     */
    public DinnerClientsAPIException(String message, HttpStatus status) {
        this.error = new ErrorDTO();
        this.error.setMessage(message);
        this.error.setErrorName(this.getClass().getSimpleName());
        this.status = status;
    }
}
