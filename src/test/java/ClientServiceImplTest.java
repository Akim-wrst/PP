import com.example.pp.clientDTO.ClientUnique;
import com.example.pp.config.AppConfig;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void testSendUniqueClientMessagesBasedOnTime() {
        ClientUnique clientUnique = new ClientUnique();
        clientUnique.setFullName("akim [aroishj asdasdas");
        clientUnique.setPhone("89581231237");
        clientUnique.setBirthday(LocalDate.of(1998, LocalDate.now().getMonth(), 12));
        clientUnique.setMessageSend(false);
        List<ClientUnique> clients = new ArrayList<>();
        clients.add(clientUnique);

        Message expectedMessage = new Message();
        expectedMessage.setPhone("89581231237");
        expectedMessage.setMessage("Test message");

        when(appConfig.getHour()).thenReturn(LocalTime.now().plusHours(1).getHour());
        when(clientUniqueRepository.findAllClientsWhereMessageSendIsFalse()).thenReturn(clients);
        when(clientUniqueMapper.clientToMessage(clientUnique, appConfig.getDiscount())).thenReturn(expectedMessage);

        clientService.sendUniqueClientMessagesBasedOnTime();

        verify(appConfig, times(1)).getHour();
        verify(clientUniqueRepository, times(1)).findAllClientsWhereMessageSendIsFalse();
        verify(clientUniqueMapper, times(1)).clientToMessage(clientUnique, appConfig.getDiscount());
        verify(kafkaTemplate, times(1)).send(eq(appConfig.getTopic_name()), eq(expectedMessage));
        verify(clientUniqueRepository, times(1)).save(clientUnique);
    }

    @Test
    public void testProcessClientAndSendUniqueMessageIfApplicable() {
        String clientId = "123";
        Client client = new Client();
        client.setPhone("12345678957");
        client.setBirthday(LocalDate.of(2024, LocalDate.now().getMonth(), 12));
        ClientUnique clientUnique = new ClientUnique();
        clientUnique.setPhone(client.getPhone());
        clientUnique.setMessageSend(false);
        Message expectedMessage = new Message();
        expectedMessage.setPhone("1234567897");
        expectedMessage.setMessage("Test message");

        when(appConfig.getHour()).thenReturn(LocalTime.now().plusHours(1).getHour());
        when(appConfig.getLastDigitOfNumber()).thenReturn("7");
        when(clientFeignClient.getClient(clientId)).thenReturn(client);
        when(clientUniqueMapper.toUniqueClient(client)).thenReturn(clientUnique);
        when(clientUniqueMapper.clientToMessage(clientUnique, appConfig.getDiscount())).thenReturn(expectedMessage);

        clientService.processClientAndSendUniqueMessageIfApplicable(clientId);

        verify(appConfig, times(1)).getHour();
        verify(appConfig, times(1)).getLastDigitOfNumber();
        verify(clientFeignClient, times(1)).getClient(clientId);
        verify(clientUniqueMapper, times(1)).toUniqueClient(client);
        verify(clientUniqueMapper, times(1)).clientToMessage(clientUnique, appConfig.getDiscount());
        verify(kafkaTemplate, times(1)).send(eq(appConfig.getTopic_name()), eq(expectedMessage));
        verify(clientUniqueRepository, times(1)).save(clientUnique);
    }
}

