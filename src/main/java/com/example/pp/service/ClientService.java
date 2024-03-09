package com.example.pp.service;

public interface ClientService {
    void sendUniqueClientMessagesBasedOnTime();

    void processClientAndSendUniqueMessageIfApplicable(String id);
}
