package com.example.pp.service;


import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.model.Client;
import com.example.pp.model.ClientUnique;
import com.example.pp.myApp.AppConfig;
import com.example.pp.repository.ClientUniqueRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.pp.kafka.KafkaProducerConfig.TOPIC_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class ClientServiceImpl {
    private final ClientUniqueRepository clientUniqueRepository;
    private final ClientUniqueMapper clientUniqueMapper;
    private final ClientFeignClient clientFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppConfig appConfig;
    private final LocalDate currentDate = LocalDate.now();
    private final LocalTime currentTime = LocalTime.now();

    @Scheduled(fixedRate = 5000)
    public void kafkaTest() {
        try {
            ClientUnique clientUnique = new ClientUnique();
            clientUnique.setFullName("Akim Parish Aleksandrovich");
            clientUnique.setPhone("89586719625");
            clientUnique.setBirthday(LocalDate.of(1998, 10, 16));
            kafkaTemplate.send(TOPIC_NAME, clientUniqueMapper.clientToMessage(clientUnique, appConfig.getDiscount()));
            System.out.println(clientUniqueMapper.clientToMessage(clientUnique, appConfig.getDiscount()));
            System.out.println(appConfig.getCron());
        } catch (Exception e) {
            log.error("Error occurred in kafkaTest method: {}", e.getMessage());
        }
    }

    public List<Client> findAllClients() {
        try {
            return Optional.ofNullable(clientFeignClient.getAllClients())
                    .map(ResponseEntity::getBody)
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occurred in findAllClients method: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void getClientUniqueInfo() {
        try {
            if (!currentTime.isAfter(LocalTime.of(19, 0))) {
                clientUniqueRepository.findAll().stream()
                        .filter(client -> !client.isMessageSend())
                        .forEach(client -> {
                            kafkaTemplate.send(TOPIC_NAME, clientUniqueMapper.clientToMessage(client, appConfig.getDiscount()));
                            client.setMessageSend(true);
                            clientUniqueRepository.save(client);
                            log.info("Message sent for clients: {}", client.getPhone());
                        });
            } else {
                log.info("Current time is past 19:00, no messages will be sent.");
            }

            clientUniqueRepository.saveAll(findAllClients()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(client -> client.getPhone().endsWith("7")
                            && client.getBirthday().getMonth() == currentDate.getMonth())
                    .map(clientUniqueMapper::toUniqueClient)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Error occurred in getClientUniqueInfo method: {}", e.getMessage());
        }
    }

    public void getClient(String id) {
        try {
            Optional.ofNullable(clientFeignClient.getClient(id).getBody())
                    .filter(client -> client.getPhone().endsWith("7"))
                    .filter(client -> client.getBirthday().getMonth() == currentDate.getMonth())
                    .map(clientUniqueMapper::toUniqueClient)
                    .ifPresent(clientUnique -> {
                        if (!currentTime.isAfter(LocalTime.of(19, 0))) {
                            kafkaTemplate.send(TOPIC_NAME, clientUniqueMapper.clientToMessage(clientUnique, appConfig.getDiscount()));
                            clientUnique.setMessageSend(true);
                            log.info("Message sent for client: {}", clientUnique.getPhone());
                        }
                        clientUniqueRepository.save(clientUnique);
                        log.info("The client is saved: {}", clientUnique.getPhone());
                    });
        } catch (Exception e) {
            log.error("Error occurred in getClient method with id {}: {}", id, e.getMessage());
        }
    }
}
