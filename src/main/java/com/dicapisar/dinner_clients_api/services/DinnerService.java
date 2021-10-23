package com.dicapisar.dinner_clients_api.services;

import com.dicapisar.dinner_clients_api.repositories.IAccountRepository;
import com.dicapisar.dinner_clients_api.repositories.IClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DinnerService implements IDinnerService {

    private IAccountRepository accountRepository;
    private IClientRepository clientRepository;

    public String generateDinner(String orderDinner) {

        return orderDinner;
    }
}
