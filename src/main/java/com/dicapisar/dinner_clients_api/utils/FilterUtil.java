package com.dicapisar.dinner_clients_api.utils;

import com.dicapisar.dinner_clients_api.dtos.ClientDTO;
import com.dicapisar.dinner_clients_api.dtos.FilterDTO;
import com.dicapisar.dinner_clients_api.dtos.TableDTO;
import com.dicapisar.dinner_clients_api.exceptions.DinnerClientsAPIException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FilterUtil {
    public static FilterDTO toFilterDTO(String orderDinner) {
        FilterDTO filterDTO = new FilterDTO();

        String[] lines = separateByLineBreak(orderDinner);

        for (String line : lines) {
            if (line.contains(">")) {
                String typeTable = line.replace(">", "");
                typeTable = typeTable.replace("\r", "");
                filterDTO.setTypeTable(typeTable);
            }
            if (line.contains("TC")) {
                String typeClient = line.replace("TC:", "");
                typeClient = typeClient.replace("\r", "");
                filterDTO.setTypeClient(Integer.parseInt(typeClient));
            }
            if (line.contains("UG")) {
                String codeGeographicLocation = line.replace("UG:", "");
                codeGeographicLocation = codeGeographicLocation.replace("\r", "");
                filterDTO.setCodeGeographicLocation(Integer.parseInt(codeGeographicLocation));
            }
            if (line.contains("RI")) {
                String initRange = line.replace("RI:", "");
                initRange = initRange.replace("\r", "");
                filterDTO.setInitRange(Integer.parseInt(initRange));
            }
            if (line.contains("RF")) {
                String finalRange = line.replace("RF:", "");
                finalRange = finalRange.replace("\r", "");
                filterDTO.setFinalRange(Integer.parseInt(finalRange));
            }
        }

        return filterDTO;
    }

    public static List<FilterDTO> toFilterDTOList(String orderDinner) {
        String[] filters = separateFilters(orderDinner);

        ArrayList<FilterDTO> filterDTOS = new ArrayList<>();

        for (int i = 1; i < filters.length; i++) {
            FilterDTO filterDTO = toFilterDTO(filters[i]);
            filterDTOS.add(filterDTO);
        }

        return filterDTOS;
    }

    private static String[] separateFilters(String orderDinner) {
        return orderDinner.split("<");
    }

    public static String[] separateByLineBreak(String string) {
        return string.split("\n");
    }

    public static List<ClientDTO> toClientDTOList(List<Object> list) {
        List<ClientDTO> clientDTOList = new ArrayList<>();

        for (Object value : list) {

            Object[] o = (Object[]) value;
            ClientDTO clientDTO = new ClientDTO();

            clientDTO.setId((int) o[0]);
            clientDTO.setCode((String) o[1]);
            clientDTO.setMale((byte) o[2] != 0);
            clientDTO.setType((int) o[3]);
            clientDTO.setLocation((String) o[4]);
            clientDTO.setCompany((String) o[5]);
            clientDTO.setEncrypt((boolean) o[6]);

            clientDTO.setTotalBalance((BigDecimal) o[7]);

            clientDTOList.add(clientDTO);
        }

        clientDTOList.sort(Collections.reverseOrder());

        return clientDTOList;
    }

    public static List<TableDTO> generateFilter(List<FilterDTO> filterDTOS, List<ClientDTO> clientDTOList) throws DinnerClientsAPIException {

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
                applyGenderLeveling(clients, getDifferenceBetweenMenAndWoman(clients));
                if (clients.size() > 8) {
                    deleteLeftoverClients(clients, (clients.size() - 8));
                    if(getDifferenceBetweenMenAndWoman(clients) != 0) {
                        applyGenderLeveling(clients, getDifferenceBetweenMenAndWoman(clients));
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

        StringBuilder stringFinal = new StringBuilder();

        for (TableDTO tableDTO :
                tableDTOList) {
            stringFinal.append("<").append(tableDTO.getType()).append(">").append("\n").append(tableDTO.getCodes()).append("\n");
        }

        return stringFinal.toString();
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
                .filter(ClientDTO::isMale)
                .collect(Collectors.toList());

        List<ClientDTO> clientDTOListFemale = clientDTOList.stream()
                .filter(x -> !x.isMale())
                .collect(Collectors.toList());

        return clientDTOListMale.size() - clientDTOListFemale.size();
    }

    private static String toStringCodes(List<ClientDTO> clientDTOList) throws DinnerClientsAPIException {
        StringBuilder code = new StringBuilder();

        for (ClientDTO clientDTO :
                clientDTOList) {

            if(clientDTO.isEncrypt()){
                clientDTO.setCode(decryptCode(clientDTO));
            }

            if(code.toString().equals("")) {
                code.append(clientDTO.getCode());
            } else {
                code.append(",").append(clientDTO.getCode());
            }
        }

        return code.toString();
    }

    private static String decryptCode(ClientDTO clientDTO) throws DinnerClientsAPIException {
        final String URL = "https://test.evalartapp.com/extapiquest/code_decrypt/" + clientDTO.getCode();
        String code;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(URL, String.class);
            assert result != null;
            code = result.replace("\"", "");
        } catch (Exception e) {
            throw new DinnerClientsAPIException("Decryption API connection failed", HttpStatus.INTERNAL_SERVER_ERROR);
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

    private static void applyGenderLeveling(List<ClientDTO> clientDTOList, int difference) {

        Collections.sort(clientDTOList);

        if (difference > 0) {
            for (int i = 0; i < difference; i++) {
                for (ClientDTO clientDTO :
                        clientDTOList) {
                    if (clientDTO.isMale()) {
                        clientDTOList.remove(clientDTO);
                        break;
                    }
                }
            }
            clientDTOList.sort(Collections.reverseOrder());
            return;
        } else if (difference < 0) {
            for (int i = 0; i < (difference * -1); i++) {
                for (ClientDTO clientDTO :
                        clientDTOList) {
                    if (!clientDTO.isMale()) {
                        clientDTOList.remove(clientDTO);
                        break;
                    }
                }
            }
            clientDTOList.sort(Collections.reverseOrder());
            return;
        }
        clientDTOList.sort(Collections.reverseOrder());
    }

    private static void deleteLeftoverClients(List<ClientDTO> clientDTOList, int countClientToDelete) {
        Collections.sort(clientDTOList);

        if (countClientToDelete > 0) {
            clientDTOList.subList(0, countClientToDelete).clear();
        }

    }
}
