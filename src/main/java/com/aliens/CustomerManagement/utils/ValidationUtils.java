package com.aliens.CustomerManagement.utils;

import com.aliens.CustomerManagement.model.Customer;
import com.aliens.CustomerManagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ValidationUtils {

    @Autowired
    CustomerRepository customerRepository;
    public void validateFile(MultipartFile file) {
        if (file.getOriginalFilename() == null ||
                !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Invalid file format. Please provide a CSV file.");
        }
    }

    public void validateCsvHeader(String headerLine) {
        String[] expectedHeader = {"customerId", "name", "email", "phone", "address", "companyName", "industryType",
                "customerStatus", "accountManager", "createdDate", "lastModifiedDate"};
        String[] actualHeader = headerLine.split(",");

        if (!Arrays.equals(expectedHeader, actualHeader)) {
            throw new IllegalArgumentException("Invalid CSV header. Please provide a valid header.");
        }
    }

    public List<Customer> validateCustomersForUpdate(List<Customer> customers) {
        List<Customer> validCustomers = new ArrayList<>();
        for(Customer customer: customers){
            Customer existingCustomer = customerRepository.findByCustomerId(customer.getCustomerId());
            if(!ObjectUtils.isEmpty(existingCustomer)){
                validCustomers.add(existingCustomer);
            }
        }
        return validCustomers;
    }
}
