package com.itrex.java.lab.projectcrmspringboot.config;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManagerFactory;

@EnableAutoConfiguration
public class CRMConfig {

    @Bean
    public SessionFactory setSessionFactory(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(SessionFactory.class);
    }

}
