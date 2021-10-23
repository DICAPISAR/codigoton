package com.dicapisar.dinner_clients_api.services;

import com.dicapisar.dinner_clients_api.dtos.ClientDTO;
import com.dicapisar.dinner_clients_api.dtos.FilterDTO;
import com.dicapisar.dinner_clients_api.repositories.IAccountRepository;
import com.dicapisar.dinner_clients_api.repositories.IClientRepository;
import com.dicapisar.dinner_clients_api.utils.FilterUtil;
import com.dicapisar.dinner_clients_api.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DinnerService implements IDinnerService {

    private IAccountRepository accountRepository;
    private IClientRepository clientRepository;

    public String generateDinner(String orderDinner) {


        List<FilterDTO> filterDTOs = FilterUtil.toFilterDTOList(orderDinner);

        List<Object> clients = clientRepository.getClientsWithTotalBalance();

        List<ClientDTO> clientDTOList = Utils.toClientDTOList(clients);

        System.out.println(clientDTOList.get(0).getTotalBalance());

        return Utils.toStringFinal(Utils.generateFilter(filterDTOs, clientDTOList));
    }
}
