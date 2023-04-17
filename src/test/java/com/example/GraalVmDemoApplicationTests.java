package com.example;

import com.example.mapper.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class GraalVmDemoApplicationTests {

    private final CustomerRepository customerRepository;

    @Autowired
    GraalVmDemoApplicationTests(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Test
    void contextLoads() {
        var size = this.customerRepository.findAll().size();
        Assert.isTrue(size > 0, () -> "there should be more than one result!");
    }

}
