package com.dicapisar.dinner_clients_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientDTO implements Comparable<ClientDTO> {
    private int id;
    private String code;
    private boolean isMale;
    private int type;
    private String location;
    private String company;
    private boolean isEncrypt;
    private BigDecimal totalBalance;

    @Override
    public int compareTo(ClientDTO c) {
        return this.getTotalBalance().compareTo(c.getTotalBalance());
    }
}
