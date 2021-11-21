package com.itrex.java.lab.projectcrmspringboot;

import com.itrex.java.lab.projectcrmspringboot.entity.User;
import com.itrex.java.lab.projectcrmspringboot.repository.TaskRepository;
import com.itrex.java.lab.projectcrmspringboot.repository.UserRepository;
import com.itrex.java.lab.projectcrmspringboot.service.TaskService;
import com.itrex.java.lab.projectcrmspringboot.service.UserService;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectCrmSpringBootApplication implements CommandLineRunner {

    @Autowired
    private Flyway flyway;

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
//        flyway.clean();
//        flyway.migrate();
//        System.out.println(userService.getAll());
//        User user = User.builder().login("112").psw("123").role(Role.builder().id(1).roleName("ADMIN").build())
//                .firstName("").lastName("").build();
        User user1 = userRepository.selectById(1);
        user1.setPsw("123444");
        System.out.println(userRepository.update(user1));
        System.out.println(userRepository.selectAll());
//        User user = User.builder().login("112").psw("123").role(Role.builder().id(1).roleName("ADMIN").build())
//                .firstName("").lastName("").build();
//        System.out.println(userRepository.add(user));
//        System.out.println(userRepository.selectAll());
//        userRepository.remove(1);
//        System.out.println(userRepository.selectAll());
//        userService.remove(1);
//        System.out.println(userRepository.selectAll());
//        flyway.clean();

//        System.out.println(taskService.finishTaskByTaskId(2));
        System.out.println("=================SHUT DOWN APP====================");
    }

}
