package com.itrex.java.lab.repository.impl.jdbc;

import com.itrex.java.lab.repository.AbstractUserRepositoryTest;
import com.itrex.java.lab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class JDBCUserRepositoryImplTest extends AbstractUserRepositoryTest {

    @Autowired
    public JDBCUserRepositoryImplTest(@Qualifier(value = "JDBCUserRepository") UserRepository userRepository) {
        super();
        postConstruct(userRepository);
    }

}
