package com.example.pp.service;


import com.example.pp.config.AppConfig;
import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.repository.ClientUniqueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientUniqueRepository clientUniqueRepository;
    private final ClientUniqueMapper clientUniqueMapper;
    private final ClientFeignClient clientFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppConfig appConfig;
    private final Month currentMonth = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).getMonth();
    private final LocalTime currentTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalTime();

    @Transactional
    @Override
    public void sendUniqueClientMessagesBasedOnTime() {
        try {
            if (!currentTime.isAfter(LocalTime.of(appConfig.getHour(), appConfig.getMinute()))) {
                clientUniqueRepository.findAllClientsWhereMessageSendIsFalse()
                        .forEach(client -> {
                            kafkaTemplate.send(appConfig.getTopic_name(), clientUniqueMapper.clientToMessage(client, appConfig.getDiscount()));
                            client.setMessageSend(true);
                            clientUniqueRepository.save(client);
                            log.info("Message sent for clients: {}", client.getPhone());
                        });
            } else {
                log.info("Current time is past, {}:{} no messages will be sent.", appConfig.getHour(), appConfig.getMinute());
            }

            clientUniqueRepository.saveAll(clientFeignClient.getAllClients()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(client -> client.getPhone().endsWith(appConfig.getLastDigitOfNumber())
                            && client.getBirthday().getMonth() == currentMonth)
                    .map(clientUniqueMapper::toUniqueClient)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Error occurred in getClientUniqueInfo method: {}", e.getMessage());
        }
    }

    @Transactional
    @Override
    public void processClientAndSendUniqueMessageIfApplicable(String id) {
        try {
            Optional.ofNullable(clientFeignClient.getClient(id))
                    .filter(client -> client.getPhone().endsWith(appConfig.getLastDigitOfNumber()))
                    .filter(client -> client.getBirthday().getMonth() == currentMonth)
                    .map(clientUniqueMapper::toUniqueClient)
                    .ifPresent(clientUnique -> {
                        if (!currentTime.isAfter(LocalTime.of(appConfig.getHour(), appConfig.getMinute()))) {
                            kafkaTemplate.send(appConfig.getTopic_name(), clientUniqueMapper.clientToMessage(clientUnique, appConfig.getDiscount()));
                            clientUnique.setMessageSend(true);
                            log.info("Message sent for client: {}", clientUnique.getPhone());
                        }
                        clientUniqueRepository.save(clientUnique);
                        log.info("The client is saved: {}", clientUnique.getPhone());
                    });
        }  catch (Exception e) {
            log.error("Error occurred in getClient method with id {}: {}", id, e.getMessage());
        }
    }
}
