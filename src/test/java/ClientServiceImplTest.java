import com.example.pp.clientDTO.ClientUnique;
import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.model.Client;
import com.example.pp.model.Message;
import com.example.pp.repository.ClientUniqueRepository;
import com.example.pp.service.ClientServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.*;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientUniqueRepository clientUniqueRepository;

    @Mock
    private ClientUniqueMapper clientUniqueMapper;

    @Mock
    private ClientFeignClient clientFeignClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void testSendUniqueClientMessagesBasedOnTime() {
        ReflectionTestUtils.setField(clientService, "time", LocalTime.now());

        Client client1 = new Client("1", "Иван", "Иванов", "Иванович", 26L, LocalDate.now(), "89111234561");
        Client client2 = new Client("2", "Петр", "Петров", "Петрович", 24L, LocalDate.now(), "89117654321");
        List<Client> clients = Arrays.asList(client1, client2);
        when(clientFeignClient.getAllClients()).thenReturn(clients);

        ClientUnique model1 = new ClientUnique("Иванов Иван Иванович", "89111234563", LocalDate.now(), false);
        ClientUnique model2 = new ClientUnique("Петров Петр Петрович", "89117654322", LocalDate.now(), false);
        when(clientUniqueRepository.findAllClientsWhereMessageSendIsFalse()).thenReturn(Arrays.asList(model1, model2));

        Message smsMessage1 = new Message("89111234567", "Текст сообщения для Иван Иванов");
        when(clientUniqueMapper.clientToMessage(any(), any())).thenReturn(smsMessage1);

        clientService.sendUniqueClientMessagesBasedOnTime();

        verify(clientFeignClient, times(1)).getAllClients();
        verify(kafkaTemplate, times(2)).send(any(), any());
        verify(clientUniqueRepository, times(2)).save(any());
    }

    /*@Test
    public void testProcessClientAndSendUniqueMessageIfApplicable() {
        ReflectionTestUtils.setField(clientService, "time", LocalTime.now());

        Client client = new Client("1", "Иван", "Иванов", "Иванович", 26L, LocalDate.now(), "89111234563");
        when(clientFeignClient.getClient("1")).thenReturn(client);

        ClientUnique model = new ClientUnique("Иванов Иван Иванович", "89111234562", LocalDate.now(), false);
        when(clientUniqueRepository.getById("89111234567")).thenReturn(model);

        Message smsMessage = new Message("89111234567", "Текст сообщения для Иван Иванов");
        when(clientUniqueMapper.clientToMessage(any(), any())).thenReturn(smsMessage);

        clientService.processClientAndSendUniqueMessageIfApplicable("1");

        verify(clientFeignClient, times(1)).getClient(any());
        verify(kafkaTemplate, times(1)).send(any(), any());
        verify(clientUniqueRepository, times(1)).save(any());
    }*/
}

