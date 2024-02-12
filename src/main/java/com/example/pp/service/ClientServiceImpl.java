package com.example.pp.service;


import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.model.Client;
import com.example.pp.repository.ClientUniqueRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClientServiceImpl {
    private final ClientUniqueRepository clientUniqueRepository;
    private final ClientUniqueMapper clientUniqueMapper;
    private final ClientFeignClient clientFeignClient;

    @Scheduled(cron = "0 0 * * * *")
    public void getClientUniqueInfo() {
        var currentDate = LocalDate.now();
        clientUniqueRepository.saveAll(findAllClients()
                .stream()
                .filter(Objects::nonNull)
                .filter(client -> client.getPhone().endsWith("7")
                        && client.getBirthday().getMonth() == currentDate.getMonth())
                .map(clientUniqueMapper::toUniqueClient)
                .collect(Collectors.toList()));
    }

    public ResponseEntity<Client> getClient(String id) {
        return Optional.ofNullable(clientFeignClient.getClient(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()).getBody();
    }

    public List<Client> findAllClients() {
        return Optional.ofNullable(clientFeignClient.getAllClients())
                .map(ResponseEntity::getBody).stream().flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
/*    public List<ClientUnique> findAllUniqueClients(){
        return clientUniqueRepository.findAll();
    }

    private static final String TOPIC_NAME = "messageSMS";
    private static final String SMS_TEMPLATE = "{}, в этом месяце для вас действует скидка {discount}";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(cron = "0 0 19 * * ?", zone = "Europe/Moscow")
    public void sendSmsNotifications() {

        // Отправка сообщений для каждого клиента
        for (ClientUnique client : findAllUniqueClients()) {
            if (!client.isMessageSend()) {
                String message = createSmsMessage(client);
                kafkaTemplate.send(TOPIC_NAME, message);
                client.setMessageSend(true);
            }
        }
    }

    private String createSmsMessage(ClientUnique client) {
        String discount = calculateDiscount(client);
        return SMS_TEMPLATE.replace("{Имя+Отчество}", client.getFullName())
                .replace("{discount}", discount);
    }

    private String calculateDiscount(ClientUnique client) {
        // Логика расчета скидки
        // Если день рождения в этом месяце, то скидка 10%, иначе 5%
        // Верните соответствующее значение скидки в виде строки
        return null;
    }*/
}
