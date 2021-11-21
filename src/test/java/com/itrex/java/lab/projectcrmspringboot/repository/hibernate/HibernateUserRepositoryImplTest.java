package com.itrex.java.lab.projectcrmspringboot.repository.hibernate;

import com.itrex.java.lab.projectcrmspringboot.repository.AbstractUserRepositoryTest;
import com.itrex.java.lab.projectcrmspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

class HibernateUserRepositoryImplTest extends AbstractUserRepositoryTest {

    @Autowired
    public HibernateUserRepositoryImplTest(UserRepository userRepository) {
        super();
        postConstruct(userRepository);
    }

}