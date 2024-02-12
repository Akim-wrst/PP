package com.example.pp.controller;

import com.example.pp.model.Client;
import com.example.pp.service.ClientServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/pp/v2/")
public class ClientController {

    private final ClientServiceImpl service;

    @GetMapping("getClient/{id}")
    @ApiOperation("Получить клиента по ID")
    public ResponseEntity<Client> getClientId(@PathVariable String id) {
        return service.getClient(id);
    }

    @GetMapping("getClient")
    @ApiOperation("Получить всех клиентов")
    public List<Client> findAllClients() {
        return service.findAllClients();
    }
}
