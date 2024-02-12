package com.example.pp.repository;

import com.example.pp.model.ClientUnique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientUniqueRepository extends JpaRepository<ClientUnique, String> {
}
