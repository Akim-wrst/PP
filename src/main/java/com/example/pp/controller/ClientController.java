package com.example.pp.controller;

import com.example.pp.service.ClientServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/clients")
@Tag(name = "Client Controller", description = "Operations related to clients")
public class ClientController {

    private final ClientServiceImpl service;

    @Operation(summary = "Send unique client messages based on time",
            description = "Sends messages to clients who have not received a message yet and the current time is before 19:00")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "500", description = "Error occurred while sending messages")
    })
    @PostMapping("clients/sendUniqueMessages")
    public void findAllClients() {
        service.sendUniqueClientMessagesBasedOnTime();
    }

    @Operation(summary = "Process client and send unique message if applicable",
            description = "Processes a client by ID, sends a unique message if the client's phone number ends with '7' and the current month matches the client's birthday month, and the current time is before 19:00")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Error occurred while processing client")
    })
    @PostMapping("client/processAndSendUniqueMessage/{id}")
    public void getClientId(@Parameter(description = "ID of the client to be processed. Cannot be empty.", required = true) @PathVariable String id) {
        service.processClientAndSendUniqueMessageIfApplicable(id);
    }
}
