package com.example.pp.httpclient;

import com.example.pp.model.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Clients", url = "${clients.url}")
public interface ClientFeignClient {
    @GetMapping("api/v1/getClient/{clientId}")
    ResponseEntity<Client> getClient(@PathVariable("clientId") String clientId);

    @GetMapping("api/v1/getClient")
    ResponseEntity<Client[]> getAllClients();
}
