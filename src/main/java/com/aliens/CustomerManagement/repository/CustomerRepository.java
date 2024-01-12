package com.aliens.CustomerManagement.repository;

import com.aliens.CustomerManagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByCustomerId(Long customerId);
}
