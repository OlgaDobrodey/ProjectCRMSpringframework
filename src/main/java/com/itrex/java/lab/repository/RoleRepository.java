package com.itrex.java.lab.repository;

import com.itrex.java.lab.entity.Role;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;

import java.util.List;

public interface RoleRepository {

    List<Role> selectAll() throws CRMProjectRepositoryException;
    Role selectById(Integer id) throws CRMProjectRepositoryException;
    Role add(Role role) throws CRMProjectRepositoryException;
    Role update(Role role) throws CRMProjectRepositoryException;

}
