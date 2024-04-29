package com.example.pp.repository;

import com.example.pp.clientDTO.ClientUnique;
import com.example.pp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientUniqueRepository extends JpaRepository<ClientUnique, String> {

    @Query("SELECT c FROM ClientUnique c WHERE c.phone = :phone")
    Client findByPhone(String phone);

    @Query("SELECT c FROM ClientUnique c WHERE c.messageSend = false")
    List<ClientUnique> findAllClientsWhereMessageSendIsFalse();

    @Modifying
    @Query("UPDATE ClientUnique c SET c.messageSend = true WHERE c.phone = :phone")
    void updateClientMessageSendTrue(@Param("phone") String phone);
}
