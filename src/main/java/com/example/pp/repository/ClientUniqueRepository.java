package com.example.pp.repository;

import com.example.pp.clientDTO.ClientUnique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientUniqueRepository extends JpaRepository<ClientUnique, String> {
    @Query("SELECT c FROM ClientUnique c WHERE c.messageSend = false")
    List<ClientUnique> findAllClientsWhereMessageSendIsFalse();
}
