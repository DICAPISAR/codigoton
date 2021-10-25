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

    /**
     * Implementation of the compareTo method to organize the ClientDTO lists by the totalBalance
     * @param c ClientDTO
     * @return 1 if it is greater, 0 if it is equal or -1 if it is less
     */
    @Override
    public int compareTo(ClientDTO c) {
        return this.getTotalBalance().compareTo(c.getTotalBalance());
    }
}
