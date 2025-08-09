package com.heartbeat.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.heartbeat")
@EntityScan(basePackages = "com.heartbeat.repo.entity")
@EnableJpaRepositories(basePackages = "com.heartbeat.repo.repository")
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class HeartbeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeartbeatApplication.class, args);
    }
}