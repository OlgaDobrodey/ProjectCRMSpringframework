package com.itrex.java.lab.projectcrmspringboot;

import com.itrex.java.lab.projectcrmspringboot.entity.User;
import com.itrex.java.lab.projectcrmspringboot.repository.TaskRepository;
import com.itrex.java.lab.projectcrmspringboot.repository.UserRepository;
import com.itrex.java.lab.projectcrmspringboot.service.TaskService;
import com.itrex.java.lab.projectcrmspringboot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ProjectCrmSpringBootApplication implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TaskService taskService;
    @Autowired
    UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(ProjectCrmSpringBootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("===================START APP======================");
//        System.out.println( userRepository.selectAll());
        User user1 = userRepository.selectById(1);
        user1.setPsw("123444");
        System.out.println(userRepository.update(user1));
        System.out.println(userRepository.selectAll());
        log.info("log info");
        log.debug("log debug");
        log.error("log error");

        System.out.println("=================SHUT DOWN APP====================");
    }

}
