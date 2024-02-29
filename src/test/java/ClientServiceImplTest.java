import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.mapper.ClientUniqueMapper;
import com.example.pp.model.Client;
import com.example.pp.model.ClientUnique;
import com.example.pp.model.Message;
import com.example.pp.myApp.AppConfig;
import com.example.pp.repository.ClientUniqueRepository;
import com.example.pp.service.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientUniqueRepository clientUniqueRepository;

    @Mock
    private ClientUniqueMapper clientUniqueMapper;

    @Mock
    private ClientFeignClient clientFeignClient;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private AppConfig appConfig;

    @Test
    public void testFindAllClients() {
        List<Client> clients = Collections.singletonList(new Client("1", "Akim", "Alexandrovich", "Parish",30L, LocalDate.of(1998, 10, 16), "89586719625"));

        when(clientFeignClient.getAllClients()).thenReturn(ResponseEntity.ok(clients));

        List<Client> result = clientService.findAllClients();

        assertEquals(clients, result);
    }

}

