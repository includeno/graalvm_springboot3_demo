package com.example.controller;

import com.example.entity.Customer;
import com.example.mapper.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class CustomerRestController {
    @Autowired
    CustomerRepository repository;

    @GetMapping("/customers")
    Collection<Customer> customers() {
        return this.repository.findAll();
    }
}
