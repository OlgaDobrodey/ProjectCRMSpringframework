package com.itrex.java.lab.projectcrmspringboot.repository.hibernate;

import com.itrex.java.lab.projectcrmspringboot.repository.AbstractRoleRepositoryTest;
import com.itrex.java.lab.projectcrmspringboot.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

class HibernateRoleRepositoryImplTest extends AbstractRoleRepositoryTest {

    @Autowired
    public HibernateRoleRepositoryImplTest(RoleRepository roleRepository) {
        super();
        postConstruct(roleRepository);
    }

}

