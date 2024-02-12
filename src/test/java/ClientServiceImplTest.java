import com.example.pp.httpclient.ClientFeignClient;
import com.example.pp.model.Client;
import com.example.pp.repository.ClientUniqueRepository;
import com.example.pp.service.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {
    @Mock
    private ClientUniqueRepository clientUniqueRepository;

    //@Mock
    //private ClientUniqueMapper clientUniqueMapper;

    @Mock
    private ClientFeignClient clientFeignClient;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void getClientUniqueInfo_ShouldSaveUniqueClients() {
        // Arrange
        Client client1 = new Client("1", "John", "Doe", "Smith", 30L, LocalDate.of(1990, 1, 1), "1234567890");
        Client client2 = new Client("2", "Jane", "Doe", "Smith", 25L, LocalDate.of(1995, 1, 1), "0987654321");
        List<Client> clients = Arrays.asList(client1, client2);
        ResponseEntity<Client[]> response = ResponseEntity.ok(clients.toArray(new Client[0]));

        when(clientFeignClient.getAllClients()).thenReturn(response);

        // Act
        clientService.getClientUniqueInfo();

        // Assert
        verify(clientUniqueRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void getClient_ShouldReturnClientById() {
        // Arrange
        String clientId = "1";
        Client client = new Client(clientId, "John", "Doe", "Smith", 30L, LocalDate.of(1990, 1, 1), "1234567890");
        ResponseEntity<Client> response = ResponseEntity.ok(client);

        when(clientFeignClient.getClient(clientId)).thenReturn(response);

        // Act
        ResponseEntity<Client> result = clientService.getClient(clientId);

        // Assert
        assertEquals(response, result);
    }

    @Test
    public void getClient_ShouldReturnNotFoundForInvalidId() {
        // Arrange
        String clientId = "invalid";
        ResponseEntity<Client> response = ResponseEntity.notFound().build();

        when(clientFeignClient.getClient(clientId)).thenReturn(response);

        // Act
        ResponseEntity<Client> result = clientService.getClient(clientId);

        // Assert
        assertEquals(response, result);
    }

    @Test
    public void findAllClients_ShouldReturnAllClients() {
        // Arrange
        Client[] clients = {
                new Client("1", "John", "Doe", "Smith", 30L, LocalDate.of(1990, 1, 1), "1234567890"),
                new Client("2", "Jane", "Doe", "Smith", 25L, LocalDate.of(1995, 1, 1), "0987654321")
        };
        ResponseEntity<Client[]> response = ResponseEntity.ok(clients);

        when(clientFeignClient.getAllClients()).thenReturn(response);

        // Act
        List<Client> result = clientService.findAllClients();

        // Assert
        assertEquals(Arrays.asList(clients), result);
    }
}

