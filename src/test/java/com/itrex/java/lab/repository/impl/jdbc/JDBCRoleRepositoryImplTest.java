package com.itrex.java.lab.repository.impl.jdbc;

import com.itrex.java.lab.repository.AbstractRoleRepositoryTest;
import com.itrex.java.lab.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class JDBCRoleRepositoryImplTest extends AbstractRoleRepositoryTest {

    @Autowired
    public JDBCRoleRepositoryImplTest(@Qualifier(value = "JDBCRoleRepository") RoleRepository roleRepository) {
        super();
        postConstruct(roleRepository);
    }

}