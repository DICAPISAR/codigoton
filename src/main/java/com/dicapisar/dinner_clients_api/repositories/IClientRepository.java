package com.dicapisar.dinner_clients_api.repositories;

import com.dicapisar.dinner_clients_api.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IClientRepository extends JpaRepository<Client, Integer> {
    @Query(value = "SELECT c.id, c.code, c.male, c.type, c.location, c.company, c.encrypt, sum(a.balance) total_balance FROM client as c join account as a on c.id = a.client_id group by c.id;", nativeQuery = true)
    List<Object> getClientsWithTotalBalance();
}
