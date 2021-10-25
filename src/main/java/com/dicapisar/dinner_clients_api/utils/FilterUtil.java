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

    /**
     * Method to pass a String to a FilterDTO object
     * @param orderDinner String to pass to FilterDTO object
     * @return FilterDTO object
     */
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

    /**
     * Method to pass a String to a FilterDTO object list
     * @param orderDinner String to pass to FilterDTO object list
     * @return FilterDTO object list
     */
    public static List<FilterDTO> toFilterDTOList(String orderDinner) {
        String[] filters = separateFilters(orderDinner);

        ArrayList<FilterDTO> filterDTOS = new ArrayList<>();

        for (int i = 1; i < filters.length; i++) {
            FilterDTO filterDTO = toFilterDTO(filters[i]);
            filterDTOS.add(filterDTO);
        }

        return filterDTOS;
    }

    /**
     * Method to pass a String to an array separating the String by the character <
     * @param orderDinner String to pass to array
     * @return String arrays
     */
    private static String[] separateFilters(String orderDinner) {
        return orderDinner.split("<");
    }

    /**
     * Method to separate a String by line breaks
     * @param string String to pass to array
     * @return String arrays
     */
    public static String[] separateByLineBreak(String string) {
        return string.split("\n");
    }

    /**
     * Method to pass a list of Object to a list of ClientDTO
     * @param list Object listing
     * @return ClientDTO listing
     */
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

    /**
     * Method that applies the filter logic to the ClientDTO list, the filters applied are: type of client,
     * geographic location code, starting range of the balance total, final range of the balance total,
     * gender leveling and only employee per company.
     *
     * When the idClient is encrypted it will use the decryptCode () method, in case there is an error with the
     * decryption API it will return DinnerClientsAPIException.
     *
     * At the end of applying the filters, the method will return a list of TableDTO.
     * @param filterDTOS FilterDTO listing
     * @param clientDTOList ClientDTO listing
     * @return TableDTO listing
     * @throws DinnerClientsAPIException Decrypt API connection error
     */
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

    /**
     * Pass the TableDTO listing to a String object applying the corresponding format;
     * <Table name>
     * IdClient listing
     * @param tableDTOList TableDTO listing
     * @return Formatted String object
     */
    public static String toStringFinal(List<TableDTO> tableDTOList) {

        StringBuilder stringFinal = new StringBuilder();

        for (TableDTO tableDTO :
                tableDTOList) {
            stringFinal.append("<").append(tableDTO.getType()).append(">").append("\n").append(tableDTO.getCodes()).append("\n");
        }

        return stringFinal.toString();
    }

    /**
     * Method that is responsible for removing those ClientDTO from the ClientDTO list that do not apply to the id of
     * the client type
     * @param clientDTOList ClientDTO listing
     * @param typeClient Customer Type id
     * @return ClientDTO listing
     */
    private static List<ClientDTO> applyFilterTypeClient(List<ClientDTO> clientDTOList, int typeClient) {
        return clientDTOList.stream()
                .filter(x -> x.getType() == typeClient)
                .collect(Collectors.toList());
    }

    /**
     * Method that is responsible for removing those ClientDTO from the ClientDTO list that do not apply to the
     * geographic location code
     * @param clientDTOList ClientDTO listing
     * @param codeGeographicLocation id geographic location code
     * @return ClientDTO listing
     */
    private static List<ClientDTO> applyFilterCodeGeographicLocation(List<ClientDTO> clientDTOList, String codeGeographicLocation) {
        return clientDTOList.stream()
                .filter(x -> x.getLocation().equals(codeGeographicLocation))
                .collect(Collectors.toList());
    }

    /**
     * Method that is responsible for removing those ClientDTOs from the ClientDTO list that do not apply to the
     * initial range of the balance total
     * @param clientDTOList ClientDTO listing
     * @param initRange initial range of balance sheet total
     * @return ClientDTO listing
     */
    private static List<ClientDTO> applyFilterInitRange(List<ClientDTO> clientDTOList, int initRange) {

        BigDecimal range = BigDecimal.valueOf(initRange);

        return clientDTOList.stream()
                .filter(x -> x.getTotalBalance().compareTo(range) > 0)
                .collect(Collectors.toList());
    }

    /**
     *  Method that is responsible for removing those ClientDTOs from the ClientDTO list that do not apply to the
     *  final range of the balance total
     * @param clientDTOList ClientDTO listing
     * @param finalRange final range of balance sheet total
     * @return ClientDTO listing
     */
    private static List<ClientDTO> applyFilterFinalRange(List<ClientDTO> clientDTOList, int finalRange) {

        BigDecimal range = BigDecimal.valueOf(finalRange);

        return clientDTOList.stream()
                .filter(x -> x.getTotalBalance().compareTo(range) < 0)
                .collect(Collectors.toList());
    }

    /**
     * Method to obtain the amount of difference between Men and Women from the ClientDTO list
     * @param clientDTOList ClientDTO listing
     * @return ClientDTO listing
     */
    private static int getDifferenceBetweenMenAndWoman(List<ClientDTO> clientDTOList) {
        List<ClientDTO> clientDTOListMale = clientDTOList.stream()
                .filter(ClientDTO::isMale)
                .collect(Collectors.toList());

        List<ClientDTO> clientDTOListFemale = clientDTOList.stream()
                .filter(x -> !x.isMale())
                .collect(Collectors.toList());

        return clientDTOListMale.size() - clientDTOListFemale.size();
    }

    /**
     * Method that is responsible for passing to String the idClient of the ClientDTO list, it relies on the
     * decryptCode() method to decrypt those idClient that are encrypted
     * @param clientDTOList ClientDTO listing
     * @return String object where the idClient is located
     * @throws DinnerClientsAPIException Decrypt API connection error
     */
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

    /**
     * Method that is responsible for connecting to the decryption API to decrypt the idClient
     * @param clientDTO ClientDTO object
     * @return String with decrypted idClient
     * @throws DinnerClientsAPIException Decrypt API connection error
     */
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

    /**
     * Method that a single ClientDTO of each company is in charge of on the ClientDTO list
     * @param clientDTOList ClientDTO listing
     * @return ClientDTO listing
     */
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

    /**
     * Method that is responsible for checking if the company of the ClientDTO that is delivered with the ClientDTO
     * list is already within the list.
     * @param companyList List of String with the id of the companies
     * @param clientDTO ClientDTO object
     * @return boolean
     */
    private static boolean isCompanyAlreadySelected(List<String> companyList, ClientDTO clientDTO) {

        for (String company :
                companyList) {
            if(company.equals(clientDTO.getCompany())){
                return true;
            }
        }
        return false;
    }

    /**
     * Method that is responsible for leveling the amount between Men and Women of the ClientDTO list.
     * @param clientDTOList ClientDTO listing
     * @param difference difference between men and women
     */
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

    /**
     * Method that is responsible for removing the ClientDTOs that are left over from the ClientDTO list
     * @param clientDTOList ClientDTO listing
     * @param countClientToDelete amount of ClientDTO to remove from the ClientDTO list
     */
    private static void deleteLeftoverClients(List<ClientDTO> clientDTOList, int countClientToDelete) {
        Collections.sort(clientDTOList);

        if (countClientToDelete > 0) {
            clientDTOList.subList(0, countClientToDelete).clear();
        }

    }
}
