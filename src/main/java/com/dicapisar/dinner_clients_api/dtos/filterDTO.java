package com.dicapisar.dinner_clients_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class filterDTO {
    private String typeTable;
    private int typeClient;
    private int codeGeographicLocation;
    private int initRange;
    private int finalRange;
}
