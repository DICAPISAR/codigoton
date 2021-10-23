package com.dicapisar.dinner_clients_api.utils;

import com.dicapisar.dinner_clients_api.dtos.ClientDTO;
import com.dicapisar.dinner_clients_api.dtos.FilterDTO;
import com.dicapisar.dinner_clients_api.dtos.TableDTO;
import com.sun.tools.jconsole.JConsoleContext;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<TableDTO> generateFilter(List<FilterDTO> filterDTOS, List<ClientDTO> clientDTOList) {

        List<TableDTO> tableDTOList = new ArrayList<>();

        for (FilterDTO filter :
                filterDTOS) {

            TableDTO tableDTO = new TableDTO();

            List<ClientDTO> clients = clientDTOList;

            if (filter.getTypeClient() != 0) {
               clients =  applyFilterTypeClient(clients, filter.getTypeClient());
            }

            if (filter.getCodeGeographicLocation() != 0) {
                clients =   applyFilterCodeGeographicLocation(clients, String.valueOf(filter.getCodeGeographicLocation()));
            }

            if (filter.getInitRange() != 0) {
                clients =  applyFilterInitRange(clients, filter.getInitRange());
            }

            if (filter.getFinalRange() != 0) {
                clients =  applyFilterFinalRange(clients, filter.getFinalRange());
            }

            tableDTO.setType(filter.getTypeTable());

            System.out.println(filter.getTypeTable());
            System.out.println(clients.size());
            System.out.println(getDifferenceBetweenMenAndWoman(clients));

            if (clients.size() < 4 ) {
                tableDTO.setCodes("CANCELADA");
            } else {
                tableDTO.setCodes(toStringCodes(clients));
            }

            tableDTOList.add(tableDTO);
        }

        return tableDTOList;
    }

    public static String toStringFinal(List<TableDTO> tableDTOList) {

        String stringFinal = "";

        for (TableDTO tableDTO :
                tableDTOList) {
            stringFinal = stringFinal  + "<" + tableDTO.getType() + ">" + "\n" + tableDTO.getCodes() + "\n";
        }

        return stringFinal;
    }





    private static List<ClientDTO> applyFilterTypeClient(List<ClientDTO> clientDTOList, int typeClient) {
        return clientDTOList.stream()
                .filter(x -> x.getType() == typeClient)
                .collect(Collectors.toList());
    }

    private static List<ClientDTO> applyFilterCodeGeographicLocation(List<ClientDTO> clientDTOList, String codeGeographicLocation) {
        return clientDTOList.stream()
                .filter(x -> x.getLocation().equals(codeGeographicLocation))
                .collect(Collectors.toList());
    }

    private static List<ClientDTO> applyFilterInitRange(List<ClientDTO> clientDTOList, int initRange) {

        BigDecimal range = BigDecimal.valueOf(initRange);

        return clientDTOList.stream()
                .filter(x -> x.getTotalBalance().compareTo(range) > 0)
                .collect(Collectors.toList());
    }

    private static List<ClientDTO> applyFilterFinalRange(List<ClientDTO> clientDTOList, int finalRange) {

        BigDecimal range = BigDecimal.valueOf(finalRange);

        return clientDTOList.stream()
                .filter(x -> x.getTotalBalance().compareTo(range) < 0)
                .collect(Collectors.toList());
    }

    private static int getDifferenceBetweenMenAndWoman(List<ClientDTO> clientDTOList) {
        List<ClientDTO> clientDTOListMale = clientDTOList.stream()
                .filter(x -> x.isMale())
                .collect(Collectors.toList());

        List<ClientDTO> clientDTOListFemale = clientDTOList.stream()
                .filter(x -> !x.isMale())
                .collect(Collectors.toList());

        return clientDTOListMale.size() - clientDTOListFemale.size();
    }

    private static String toStringCodes(List<ClientDTO> clientDTOList) {
        String code = "";

        for (ClientDTO clientDTO :
                clientDTOList) {

            if(clientDTO.isEncrypt()){
                clientDTO.setCode(decryptCode(clientDTO));
            }

            if(code.equals("")) {
                code = code + clientDTO.getCode();
            }
            code = code + "," + clientDTO.getCode();
        }

        return code;
    }

    private static String decryptCode(ClientDTO clientDTO) {
        final String URL = "https://test.evalartapp.com/extapiquest/code_decrypt/" + clientDTO.getCode();
        String code = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(URL, String.class);
            code = result;
        } catch (Exception e) {
            //TODO: RESPONDER CON ERROR DE CONEXION A LA API DE DESENCRIPTAR
        }

        return code;
    }

}