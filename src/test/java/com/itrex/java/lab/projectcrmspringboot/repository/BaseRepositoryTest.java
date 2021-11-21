package com.itrex.java.lab.projectcrmspringboot.repository;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class BaseRepositoryTest {

    @Autowired
    private Flyway flyway;

    @BeforeEach
    public void initDB() {
        flyway.migrate();
    }

    @AfterEach
    public void cleanDB() {
        flyway.clean();
    }

}

