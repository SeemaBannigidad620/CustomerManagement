package com.aliens.CustomerManagement.controller;

import com.aliens.CustomerManagement.model.Customer;
import com.aliens.CustomerManagement.service.CustomerService;
import com.aliens.CustomerManagement.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/api/customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    ValidationUtils validationUtils;

    @PostMapping("/create")
    public ResponseEntity<Object> createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping("/findAll")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}/update")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable long id) {
        customerService.deleteCustomer(id);
    }

    @PostMapping("/bulk-load")
    public ResponseEntity<String> bulkLoad(@RequestParam("file") MultipartFile file) {
        List<Customer> customers = customerService.readCustomersFromFile(file);
        List<Customer> savedCustomers = customerService.bulkLoad(customers);
        if (!ObjectUtils.isEmpty(savedCustomers)) {
            return new ResponseEntity<>("Bulk load successful", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Bulk load is not successful", HttpStatus.EXPECTATION_FAILED);
    }


    @PutMapping("/bulk-update")
    public ResponseEntity<String> bulkUpdate(@RequestParam("file") MultipartFile file) {
        try {
            List<Customer> customers = customerService.readCustomersFromFile(file);

            // Validate the list of customers
            List<Customer> existingCustomer = validationUtils.validateCustomersForUpdate(customers);

            List<Customer> updatedCustomers = customerService.bulkUpdate(existingCustomer);

            return new ResponseEntity<>("Bulk update successful. ", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
