package com.example.config;

import com.example.entity.Customer;
import com.example.mapper.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Configuration
public class Initializr implements ApplicationRunner {
    @Autowired
    CustomerRepository repository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Stream.of("A", "B", "C", "D")
                .map(c -> new Customer(null, c, LocalDateTime.now()))
                .map(this.repository::save)
                .forEach(System.out::println);
    }
}
