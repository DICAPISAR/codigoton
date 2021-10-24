package com.dicapisar.dinner_clients_api.services;

import com.dicapisar.dinner_clients_api.exceptions.DinnerClientsAPIException;

public interface IDinnerService {
    String generateDinner(String orderDinner) throws DinnerClientsAPIException;
}
