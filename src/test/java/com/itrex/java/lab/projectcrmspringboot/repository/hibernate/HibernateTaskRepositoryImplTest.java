package com.itrex.java.lab.projectcrmspringboot.repository.hibernate;

import com.itrex.java.lab.projectcrmspringboot.repository.AbstractTaskRepositoryTest;
import com.itrex.java.lab.projectcrmspringboot.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

class HibernateTaskRepositoryImplTest extends AbstractTaskRepositoryTest {

    @Autowired
    public HibernateTaskRepositoryImplTest(TaskRepository taskRepository) {
        super();
        postConstruct(taskRepository);
    }

}