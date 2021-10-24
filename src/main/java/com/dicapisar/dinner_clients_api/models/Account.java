package com.dicapisar.dinner_clients_api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "client_id")
    private int clientId;

    @Column(name = "balance")
    private float balance;
}
