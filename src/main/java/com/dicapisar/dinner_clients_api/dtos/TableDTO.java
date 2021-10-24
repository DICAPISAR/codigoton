package com.dicapisar.dinner_clients_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TableDTO {
    private String type;
    private String codes;
}
