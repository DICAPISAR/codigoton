package com.dicapisar.dinner_clients_api.services;

import com.dicapisar.dinner_clients_api.dtos.ClientDTO;
import com.dicapisar.dinner_clients_api.dtos.FilterDTO;
import com.dicapisar.dinner_clients_api.exceptions.DinnerClientsAPIException;
import com.dicapisar.dinner_clients_api.repositories.IClientRepository;
import com.dicapisar.dinner_clients_api.utils.FilterUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DinnerService implements IDinnerService {

    private IClientRepository clientRepository;

    public String generateDinner(String orderDinner) throws DinnerClientsAPIException {

        List<FilterDTO> filterDTOs = FilterUtil.toFilterDTOList(orderDinner);

        List<Object> clients = clientRepository.getClientsWithTotalBalance();

        List<ClientDTO> clientDTOList = FilterUtil.toClientDTOList(clients);

        return FilterUtil.toStringFinal(FilterUtil.generateFilter(filterDTOs, clientDTOList));
    }
}
