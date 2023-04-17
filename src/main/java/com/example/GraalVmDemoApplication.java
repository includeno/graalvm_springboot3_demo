package com.example;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
public class GraalVmDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraalVmDemoApplication.class, args);
    }

    @PostConstruct
    void started() {
        // 设置用户时区为 UTC
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
    }
}
