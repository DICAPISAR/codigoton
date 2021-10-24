package com.dicapisar.dinner_clients_api.repositories;

import com.dicapisar.dinner_clients_api.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Integer> {
}
