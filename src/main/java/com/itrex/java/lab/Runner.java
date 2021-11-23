package com.itrex.java.lab;

import com.itrex.java.lab.config.CRMContextConfiguration;
import com.itrex.java.lab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

@Slf4j
public class Runner {

    public static void main(String[] args) {
        try {
            System.out.println("===================START APP======================");

            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "info_log");
            ApplicationContext context = new AnnotationConfigApplicationContext(CRMContextConfiguration.class);

            UserService service = context.getBean(UserService.class);
            System.out.println(service.getAllUsersByTaskId(2));

            log.info("info log");
            log.error("error log");
            log.debug("debug log");
        } catch (Exception e) {
            log.info("THE END..", e);
        }
        System.out.println("================CLOSE APP===================");
    }

}

