package com.aliens.CustomerManagement.service;

import com.aliens.CustomerManagement.exception.CustomerNotFoundException;
import com.aliens.CustomerManagement.model.Customer;
import com.aliens.CustomerManagement.repository.CustomerRepository;
import com.aliens.CustomerManagement.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ValidationUtils validationUtils;

    public ResponseEntity<Object> createCustomer(Customer customer) {
        try {
            Customer existsCustomer = customerRepository.findByCustomerId(customer.getCustomerId());
            if (!ObjectUtils.isEmpty(existsCustomer)) {
                throw new RuntimeException("Customer already exists for the ID " + customer.getCustomerId());
            }
            customerRepository.save(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findByCustomerId(id);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        Customer existingCustomer = customerRepository.findByCustomerId(id);
        if (!ObjectUtils.isEmpty(existingCustomer)) {
            customerRepository.save(customer);
        } else {
            throw new CustomerNotFoundException(HttpStatus.NOT_FOUND, "Customer not found with the Id " + id);
        }
        return customer;
    }

    public void deleteCustomer(long id) {
        Customer existingCustomer = customerRepository.findByCustomerId(id);
        if (ObjectUtils.isEmpty(existingCustomer)) {
            throw new CustomerNotFoundException(HttpStatus.NOT_FOUND, "Customer Does Not Exists");
        } else {
            customerRepository.delete(existingCustomer);
        }
    }

    public List<Customer> readCustomersFromFile(MultipartFile file) {
        List<Customer> customers = new ArrayList<>();

        validationUtils.validateFile(file);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = br.readLine(); // Assuming the first line is the header
            validationUtils.validateCsvHeader(headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Customer customer = createCustomerFromCsv(data);
                customers.add(customer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return customers;

    }

    private Customer createCustomerFromCsv(String[] data) {
        Customer customer = new Customer();
        customer.setCustomerId(Long.parseLong(data[0])); // Assuming customerId is of type Long
        customer.setName(data[1]);
        customer.setEmail(data[2]);
        customer.setPhone(data[3]);
        customer.setAddress(data[4]);
        customer.setCompanyName(data[5]);
        customer.setIndustryType(data[6]);
        customer.setCustomerStatus(data[7]);
        customer.setAccountManager(data[8]);
        return customer;
    }

    public List<Customer> bulkLoad(List<Customer> customers) {
        return customerRepository.saveAll(customers);
    }

    public List<Customer> bulkUpdate(List<Customer> customers) {
        return customerRepository.saveAll(customers);
    }
}
