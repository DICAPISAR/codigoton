package com.dicapisar.dinner_clients_api.services;

import com.dicapisar.dinner_clients_api.dtos.FilterDTO;
import com.dicapisar.dinner_clients_api.repositories.IAccountRepository;
import com.dicapisar.dinner_clients_api.repositories.IClientRepository;
import com.dicapisar.dinner_clients_api.utils.FilterUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DinnerService implements IDinnerService {

    private IAccountRepository accountRepository;
    private IClientRepository clientRepository;

    public String generateDinner(String orderDinner) {


        List<FilterDTO> filterDTOs = FilterUtil.toFilterDTOList(orderDinner);

        FilterDTO filterDTO = filterDTOs.get(0);

        System.out.println(filterDTO.getTypeTable());
        System.out.println(filterDTO.getTypeClient());
        System.out.println(filterDTO.getCodeGeographicLocation());
        System.out.println(filterDTO.getInitRange());
        System.out.println(filterDTO.getFinalRange());


        return orderDinner;
    }
}
