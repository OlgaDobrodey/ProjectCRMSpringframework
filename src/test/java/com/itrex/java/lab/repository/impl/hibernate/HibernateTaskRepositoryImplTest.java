package com.itrex.java.lab.repository.impl.hibernate;

import com.itrex.java.lab.repository.AbstractTaskRepositoryTest;
import com.itrex.java.lab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

class HibernateTaskRepositoryImplTest extends AbstractTaskRepositoryTest {

    @Autowired
    public HibernateTaskRepositoryImplTest(TaskRepository taskRepository) {
        super();
        postConstruct(taskRepository);
    }

}