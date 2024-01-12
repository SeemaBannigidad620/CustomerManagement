package com.aliens.CustomerManagement.service;

import com.aliens.CustomerManagement.exception.CustomerNotFoundException;
import com.aliens.CustomerManagement.model.Customer;
import com.aliens.CustomerManagement.repository.CustomerRepository;
import com.aliens.CustomerManagement.utils.ValidationUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ValidationUtils validationUtils;

    @InjectMocks
    private CustomerService customerService;

    EasyRandom generator = new EasyRandom();

    List<Customer> randomCustomers = generator.objects(Customer.class, 5)
            .collect(Collectors.toList());

    List<Customer> customers = new ArrayList<>();
    Customer customer = new Customer();

    @BeforeEach
    public void setUp() {
        customer.setCustomerId(1673l);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("1234567890");
    }

    @Test
    void createCustomer() {
        Customer savedCustomer = (Customer) customerService.createCustomer(customer).getBody();
        assertEquals(savedCustomer, customer);
    }

    @Test
    void getAllCustomers() {
        List<Customer> customerList = customerService.getAllCustomers();
        assertEquals(customerList, customers);
    }

    @Test
    void getCustomerById() {
        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(customer);
        Customer savedCustomer = customerService.getCustomerById(customer.getCustomerId());
        assertEquals(savedCustomer.getCustomerId(), customer.getCustomerId());
    }

    @Test
    void updateCustomer() {
        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(customer);
        Customer savedCustomer = customerService.updateCustomer(customer.getCustomerId(), customer);
        assertEquals(savedCustomer.getCustomerId(), customer.getCustomerId());
    }

    @Test
    void deleteCustomer_ThrowsException() {
        when(customerRepository.findByCustomerId(customer.getCustomerId())).thenReturn(null);
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(customer.getCustomerId()));
    }

    @Test
    void readCustomersFromFile() {
        MockMultipartFile file = createMockMultipartFile("1,John Doe,john@example.com,1234567890,Address1,Company1," +
                "Industry1,Active,Manager1\n2,Jane Doe,jane@example.com,9876543210,Address2,Company2,Industry2,Inactive,Manager2");


        List<Customer> expectedCustomers = generator.objects(Customer.class, 1).collect(Collectors.toList());

        List<Customer> actualCustomers = customerService.readCustomersFromFile(file);

        assertEquals(expectedCustomers.size(), actualCustomers.size());


    }

    private MockMultipartFile createMockMultipartFile(String content) {
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        try {
            return new MockMultipartFile("file", "test.csv", "text/csv", inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void bulkLoad() {
        List<Customer> customersToLoad = generator.objects(Customer.class, 2).collect(Collectors.toList());

        when(customerRepository.saveAll(any())).thenReturn(customersToLoad);

        List<Customer> loadedCustomers = customerService.bulkLoad(customersToLoad);

        assertEquals(customersToLoad.size(), loadedCustomers.size());
    }

    @Test
    void bulkUpdate() {
        List<Customer> customersToUpdate = generator.objects(Customer.class, 2).collect(Collectors.toList());

        when(customerRepository.saveAll(any())).thenReturn(customersToUpdate);

        List<Customer> updatedCustomers = customerService.bulkUpdate(customersToUpdate);

        assertEquals(customersToUpdate.size(), updatedCustomers.size());
    }
}
