package com.itrex.java.lab.repository;

import com.itrex.java.lab.config.CRMTestContextConfiguration;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(CRMTestContextConfiguration.class)
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

