package com.dicapisar.dinner_clients_api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "client")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code")
    private String code;

    @Column(name = "male")
    private boolean isMale;

    @Column(name = "type")
    private int type;

    @Column(name = "location")
    private String location;

    @Column(name = "company")
    private String company;

    @Column(name = "encrypt")
    private boolean isEncrypt;
}
