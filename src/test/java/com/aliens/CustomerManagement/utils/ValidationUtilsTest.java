package com.aliens.CustomerManagement.utils;

import com.aliens.CustomerManagement.model.Customer;
import com.aliens.CustomerManagement.repository.CustomerRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsTest {

    @InjectMocks
    ValidationUtils validationUtils;

    @Mock
    CustomerRepository customerRepository;

    @Test
    void testValidateFileWithInvalidFormat() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
                "file content".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validationUtils.validateFile(file));

        assertTrue(exception.getMessage().contains("Invalid file format"));
    }

    @Test
    void testValidateFileWithValidFormat() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
                "file content".getBytes());

        assertDoesNotThrow(() -> validationUtils.validateFile(file));
    }


    @Test
    void testValidateCsvHeaderWithInvalidHeader() {
        String headerLine = "invalidHeader1,invalidHeader2,invalidHeader3";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validationUtils.validateCsvHeader(headerLine));

        assertTrue(exception.getMessage().contains("Invalid CSV header"));
    }

    @Test
    void testValidateCsvHeaderWithValidHeader() {
        String headerLine = "customerId,name,email,phone,address,companyName,industryType," +
                "customerStatus,accountManager,createdDate,lastModifiedDate";

        assertDoesNotThrow(() -> validationUtils.validateCsvHeader(headerLine));
    }

    @Test
    void testValidateCustomersForUpdate() {
        EasyRandom easyRandom = new EasyRandom();
        List<Customer> inputCustomers = easyRandom.objects(Customer.class, 2).collect(Collectors.toList());

        when(customerRepository.findByCustomerId(any())).thenReturn(inputCustomers.get(0));
        when(customerRepository.findByCustomerId(any())).thenReturn(inputCustomers.get(1));

        List<Customer> validCustomers = validationUtils.validateCustomersForUpdate(inputCustomers);

        assertTrue(validCustomers.size() == inputCustomers.size());
    }
}