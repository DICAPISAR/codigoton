package com.dicapisar.dinner_clients_api.utils;

import com.dicapisar.dinner_clients_api.dtos.ClientDTO;
import com.dicapisar.dinner_clients_api.dtos.FilterDTO;
import com.dicapisar.dinner_clients_api.dtos.TableDTO;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        Collections.sort(clientDTOList, Collections.reverseOrder());

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

            clients = removeRepeatEmployeesByCompany(clients);

            tableDTO.setType(filter.getTypeTable());

            if (clients.size() < 4 ) {
                tableDTO.setCodes("CANCELADA");
            } else {
                clients = applyGenderLeveling(clients, getDifferenceBetweenMenAndWoman(clients));
                if (clients.size() > 8) {
                    clients = deleteLeftoverClients(clients, (clients.size()-8));
                    if(getDifferenceBetweenMenAndWoman(clients) != 0) {
                        clients = applyGenderLeveling(clients, getDifferenceBetweenMenAndWoman(clients));
                    }
                }
                if(clients.size() < 4) {
                    tableDTO.setCodes("CANCELADA");
                } else {
                    tableDTO.setCodes(toStringCodes(clients));
                }
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
            } else {
                code = code + "," + clientDTO.getCode();
            }
        }

        return code;
    }

    private static String decryptCode(ClientDTO clientDTO) {
        final String URL = "https://test.evalartapp.com/extapiquest/code_decrypt/" + clientDTO.getCode();
        String code = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(URL, String.class);
            code = result.replace("\"", "");
        } catch (Exception e) {
            //TODO: RESPONDER CON ERROR DE CONEXION A LA API DE DESENCRIPTAR
        }

        return code;
    }

    private static List<ClientDTO> removeRepeatEmployeesByCompany(List<ClientDTO> clientDTOList) {
        List<ClientDTO> clientDTOS = new ArrayList<>();

        List<String> companyList = new ArrayList<>();

        for (ClientDTO clientDTO :
                clientDTOList) {
            if (!isCompanyAlreadySelected(companyList, clientDTO)){
                clientDTOS.add(clientDTO);
                companyList.add(clientDTO.getCompany());
            }
        }

        return clientDTOS;
    }

    private static boolean isCompanyAlreadySelected(List<String> companyList, ClientDTO clientDTO) {

        for (String company :
                companyList) {
            if(company.equals(clientDTO.getCompany())){
                return true;
            }
        }
        return false;
    }

    private static List<ClientDTO> applyGenderLeveling(List<ClientDTO> clientDTOList, int difference) {

        Collections.sort(clientDTOList);

        if (difference > 0) {
            for (int i = 0; i < difference; i++) {
                for (ClientDTO clientDTO :
                        clientDTOList) {
                    if (clientDTO.isMale()) {
                        int position = clientDTOList.indexOf(clientDTO);
                        clientDTOList.remove(position);
                        break;
                    }
                }
            }
            Collections.sort(clientDTOList, Collections.reverseOrder());
            return clientDTOList;
        } else if (difference < 0) {
            for (int i = 0; i < (difference * -1); i++) {
                for (ClientDTO clientDTO :
                        clientDTOList) {
                    if (!clientDTO.isMale()) {
                        int position = clientDTOList.indexOf(clientDTO);
                        clientDTOList.remove(position);
                        break;
                    }
                }
            }
            Collections.sort(clientDTOList, Collections.reverseOrder());
            return clientDTOList;
        }
        Collections.sort(clientDTOList, Collections.reverseOrder());
        return clientDTOList;
    }

    private static List<ClientDTO> deleteLeftoverClients(List<ClientDTO> clientDTOList, int countClientToDelete) {
        Collections.sort(clientDTOList);

        for (int i = 0; i < countClientToDelete; i++) {
            clientDTOList.remove(0);
        }

        return clientDTOList;
    }
}