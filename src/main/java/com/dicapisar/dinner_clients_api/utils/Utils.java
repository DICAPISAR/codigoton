package com.dicapisar.dinner_clients_api.utils;

import com.dicapisar.dinner_clients_api.dtos.ClientDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String[] separateByLineBreak(String string) {
        return string.split("\n");
    }

    public static List<ClientDTO> toClientDTOList(List<Object> list) {
        List<ClientDTO> clientDTOList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            Object[] o = (Object[]) list.get(i);
            ClientDTO clientDTO = new ClientDTO();

            clientDTO.setId((int) o[0]);
            clientDTO.setCode((String) o[1]);
            if((byte) o[2] == 0){
                clientDTO.setMale(false);
            } else {
                clientDTO.setMale(true);
            }
            clientDTO.setType((int) o[3]);
            clientDTO.setLocation((String) o[4]);
            clientDTO.setCompany((String) o[5]);
            if((boolean) o[6]){
                clientDTO.setEncrypt(true);
            } else {
                clientDTO.setEncrypt(false);
            }

            clientDTO.setTotalBalance((BigDecimal) o[7]);

            clientDTOList.add(clientDTO);
        }

        return clientDTOList;
    }
}
