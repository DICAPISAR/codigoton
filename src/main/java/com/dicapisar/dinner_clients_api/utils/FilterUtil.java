package com.dicapisar.dinner_clients_api.utils;

import com.dicapisar.dinner_clients_api.dtos.FilterDTO;

import java.util.ArrayList;
import java.util.List;

public class FilterUtil {
    public static FilterDTO toFilterDTO(String orderDinner) {
        FilterDTO filterDTO = new FilterDTO();

        String lines[] = Utils.separateByLineBreak(orderDinner);

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains(">")){
                String typeTable = lines[i].replace(">","");
                typeTable = typeTable.replace("\r", "");
                filterDTO.setTypeTable(typeTable);
            }
            if (lines[i].contains("TC")){
                String typeClient = lines[i].replace("TC:", "");
                typeClient = typeClient.replace("\r", "");
                filterDTO.setTypeClient(Integer.parseInt(typeClient));
            }
            if (lines[i].contains("UG")){
                String codeGeographicLocation = lines[i].replace("UG:", "");
                codeGeographicLocation = codeGeographicLocation.replace("\r", "");
                filterDTO.setCodeGeographicLocation(Integer.parseInt(codeGeographicLocation));
            }
            if (lines[i].contains("RI")){
                String initRange = lines[i].replace("RI:", "");
                initRange = initRange.replace("\r", "");
                filterDTO.setInitRange(Integer.parseInt(initRange));
            }
            if (lines[i].contains("RF")){
                String finalRange = lines[i].replace("RF:", "");
                finalRange = finalRange.replace("\r", "");
                filterDTO.setFinalRange(Integer.parseInt(finalRange));
            }
        }

        return filterDTO;
    }

    public static List<FilterDTO> toFilterDTOList(String orderDinner) {
        String filters[] = separateFiltes(orderDinner);

        ArrayList<FilterDTO> filterDTOS = new ArrayList<>();

        for (int i = 1; i < filters.length; i++) {
            FilterDTO filterDTO = toFilterDTO(filters[i]);
            filterDTOS.add(filterDTO);
        }

        return filterDTOS;
    }

    private static String[] separateFiltes(String orderDinner) {
        return orderDinner.split("<");
    }
}
