import com.example.pp.clientDTO.ClientUnique;
import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.model.Client;
import com.example.pp.model.Message;
import com.example.pp.repository.ClientUniqueRepository;
import com.example.pp.service.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

        when(clientFeignClient.getAllClients()).thenReturn(Arrays.asList(
                new Client("1", "Иван", "Иванов", "Иванович", 26L, LocalDate.now(), "89111234561"),
                new Client("2", "Петр", "Петров", "Петрович", 24L, LocalDate.now(), "89117654321")
        ));

        when(clientUniqueRepository.findAllClientsWhereMessageSendIsFalse()).thenReturn(Arrays.asList(
                new ClientUnique("Иванов Иван Иванович", "89111234563", LocalDate.now(), false),
                new ClientUnique("Петров Петр Петрович", "89117654322", LocalDate.now(), false)
        ));

        when(clientUniqueRepository.findPhoneByPhone(anyString())).thenReturn(null);

        when(clientUniqueMapper.clientToMessage(any(), any())).thenReturn(
                new Message("89111234567", "Текст сообщения для Иван Иванов"));

        clientService.sendUniqueClientMessagesBasedOnTime();

        verify(clientFeignClient, times(1)).getAllClients();

        verify(clientUniqueMapper, times(2)).clientToMessage(any(), any());

        verify(kafkaTemplate, times(2)).send(anyString(), any());

        verify(clientUniqueRepository, times(2)).findPhoneByPhone(anyString());

        verify(clientUniqueRepository, times(2)).updateClientMessageSendTrue(anyString());

        verify(clientUniqueRepository, times(2)).save(any());
    }

    @Test
    public void testProcessClientAndSendUniqueMessageIfApplicable() {
        ReflectionTestUtils.setField(clientService, "currentTime", LocalTime.now());
        ReflectionTestUtils.setField(clientService, "discount", "10%");
        ReflectionTestUtils.setField(clientService, "topic_name", "test_topic");
        ReflectionTestUtils.setField(clientService, "lastDigitOfNumber", "7");
        ReflectionTestUtils.setField(clientService, "time", LocalTime.now());

        Client client = new Client("1", "Иван", "Иванов", "Иванович", 26L, LocalDate.now(), "89111234567");
        ClientUnique clientUnique = new ClientUnique("Иванов Иван Иванович", "89111234567", LocalDate.now(), false);

        when(clientFeignClient.getClient("1")).thenReturn(client);
        when(clientUniqueMapper.toUniqueClient(client)).thenReturn(clientUnique);

        clientService.processClientAndSendUniqueMessageIfApplicable("1");

        verify(clientFeignClient, times(1)).getClient("1");
        verify(clientUniqueMapper, times(1)).toUniqueClient(client);
        verify(clientUniqueRepository, times(1)).save(any(ClientUnique.class));
        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }
}

