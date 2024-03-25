package com.example.pp.httpclient;

import com.example.pp.model.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "Clients", url = "${clients.url}")
public interface ClientFeignClient {
    @PostMapping("api/v1/getClient/{clientId}")
    Client getClient(@PathVariable("clientId") String clientId);

    @PostMapping("api/v1/getClient")
    List<Client> getAllClients();
}
