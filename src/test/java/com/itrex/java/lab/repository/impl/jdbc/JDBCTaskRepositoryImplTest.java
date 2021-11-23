package com.itrex.java.lab.repository.impl.jdbc;

import com.itrex.java.lab.repository.AbstractTaskRepositoryTest;
import com.itrex.java.lab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class JDBCTaskRepositoryImplTest extends AbstractTaskRepositoryTest {

    @Autowired
    public JDBCTaskRepositoryImplTest(@Qualifier(value = "JDBCTaskRepository") TaskRepository taskRepository) {
        super();
        postConstruct(taskRepository);
    }

}