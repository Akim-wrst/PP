package com.example.pp.service;

import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.model.Message;
import com.example.pp.repository.ClientUniqueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientUniqueRepository clientUniqueRepository;
    private final ClientUniqueMapper clientUniqueMapper;
    private final ClientFeignClient clientFeignClient;
    private final KafkaTemplate<String, Message> kafkaTemplate;

    private final Month currentMonth = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).getMonth();
    private final LocalTime currentTime = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalTime();

    @Value("${clients.discount}")
    private String discount;

    @Value("${clients.topic_name}")
    private String topic_name;

    @Value("${clients.lastDigitOfNumber}")
    private String lastDigitOfNumber;

    @Value("${clients.time}")
    private LocalTime time;

    @Transactional
    @Override
    public void sendUniqueClientMessagesBasedOnTime() {
        try {
            if (!currentTime.isAfter(time)) {
                clientUniqueRepository.findAllClientsWhereMessageSendIsFalse()
                        .forEach(client -> {
                            kafkaTemplate.send(topic_name, clientUniqueMapper.clientToMessage(client, discount));
                            clientUniqueRepository.updateClientMessageSendTrue(client.getPhone());
                            log.info("Message sent for clients: {}", client.getPhone());
                        });
            } else {
                log.info("Current time is past, {} no messages will be sent.", time);
            }

            clientFeignClient.getAllClients()
                    .parallelStream()
                    .filter(Objects::nonNull)
                    .filter(client -> client.getPhone().endsWith(lastDigitOfNumber)
                            && client.getBirthday().getMonth() == currentMonth)
                    .filter(client -> clientUniqueRepository.findPhoneByPhone(client.getPhone()) == null)
                    .forEach(client -> clientUniqueRepository.save(clientUniqueMapper.toUniqueClient(client)));

        } catch (Exception e) {
            log.error("Error occurred in getClientUniqueInfo method: {}", e.getMessage());
        }
    }

    @Transactional
    @Override
    public void processClientAndSendUniqueMessageIfApplicable(String id) {
        try {
            Optional.ofNullable(clientFeignClient.getClient(id))
                    .filter(client -> client.getPhone().endsWith(lastDigitOfNumber)
                            && client.getBirthday().getMonth() == currentMonth)
                    .map(clientUniqueMapper::toUniqueClient)
                    .ifPresent(clientUnique -> {
                        if (!currentTime.isAfter(time)) {
                            kafkaTemplate.send(topic_name, clientUniqueMapper.clientToMessage(clientUnique, discount));
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
