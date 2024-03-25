package com.example.pp.controller;

import com.example.pp.service.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    @Mock
    private ClientServiceImpl service;

    @InjectMocks
    private ClientController controller;

    @Test
    public void testFindAllClients() {
        controller.findAllClients();

        verify(service).sendUniqueClientMessagesBasedOnTime();
    }

    @Test
    public void testGetClientId() {
        controller.getClientId("123");

        verify(service).processClientAndSendUniqueMessageIfApplicable("123");
    }
}