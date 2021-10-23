package com.dicapisar.dinner_clients_api.repositories;

import com.dicapisar.dinner_clients_api.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClientRepository extends JpaRepository<Client, Integer> {
}
