package com.erikandreas.configserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EnvTester implements CommandLineRunner {

    @Autowired
    private Environment env;

    @Override
    public void run(String... args) {
        log.info("Testing environment variables:");
        log.info("POSTGRES_USER = {}", env.getProperty("POSTGRES_USER", "not_found"));
        log.info("POSTGRES_PORT = {}", env.getProperty("POSTGRES_PORT", "not_found"));
        log.info("MONGO_INITDB_ROOT_USERNAME = {}", env.getProperty("MONGO_INITDB_ROOT_USERNAME", "not_found"));
    }
}