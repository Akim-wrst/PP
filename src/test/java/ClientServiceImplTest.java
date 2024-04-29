import com.example.pp.clientDTO.ClientUnique;
import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.model.Client;
import com.example.pp.model.Message;
import com.example.pp.repository.ClientUniqueRepository;
import com.example.pp.service.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

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
    void testSendUniqueClientMessagesBasedOnTime() {
        ReflectionTestUtils.setField(clientService, "currentTime", LocalTime.now());
        ReflectionTestUtils.setField(clientService, "discount", "10%");
        ReflectionTestUtils.setField(clientService, "topic_name", "test_topic");
        ReflectionTestUtils.setField(clientService, "lastDigitOfNumber", "1");
        ReflectionTestUtils.setField(clientService, "time", LocalTime.now());

        Client client1 = new Client("1", "Иван", "Иванов", "Иванович", 26L, LocalDate.now(), "89111234561");
        Client client2 = new Client("2", "Петр", "Петров", "Петрович", 24L, LocalDate.now(), "89117654321");
        when(clientFeignClient.getAllClients()).thenReturn(Arrays.asList(client1, client2));

        ClientUnique model1 = new ClientUnique("Иванов Иван Иванович", "89111234563", LocalDate.now(), false);
        ClientUnique model2 = new ClientUnique("Петров Петр Петрович", "89117654322", LocalDate.now(), false);

        when(clientUniqueRepository.findAllClientsWhereMessageSendIsFalse()).thenReturn(Arrays.asList(model1, model2));
        when(clientUniqueRepository.findPhoneByPhone(anyString())).thenReturn(null);

        Message smsMessage1 = new Message("89111234567", "Текст сообщения для Иван Иванов");
        when(clientUniqueMapper.clientToMessage(any(), any())).thenReturn(smsMessage1);

        clientService.sendUniqueClientMessagesBasedOnTime();

        verify(clientFeignClient, times(1)).getAllClients();

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate, times(2)).send(topicCaptor.capture(), messageCaptor.capture());

        ArgumentCaptor<String> phoneCaptor = ArgumentCaptor.forClass(String.class);
        verify(clientUniqueRepository, times(2)).updateClientMessageSendTrue(phoneCaptor.capture());

        ArgumentCaptor<ClientUnique> clientCaptor = ArgumentCaptor.forClass(ClientUnique.class);
        verify(clientUniqueRepository, times(2)).save(clientCaptor.capture());
    }

    @Test
    public void testProcessClientAndSendUniqueMessageIfApplicable() {
        Client client = new Client("1", "Иван", "Иванов", "Иванович", 26L, LocalDate.now(), "89111234567");

        when(clientFeignClient.getClient("1")).thenReturn(client);

        clientService.processClientAndSendUniqueMessageIfApplicable("1");

        verify(clientFeignClient, times(1)).getClient("1");
    }
}

