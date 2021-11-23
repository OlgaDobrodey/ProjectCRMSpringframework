package com.itrex.java.lab.repository;

import com.itrex.java.lab.entity.User;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;

import java.util.List;

public interface UserRepository {

    List<User> selectAll() throws CRMProjectRepositoryException;
    User selectById(Integer id) throws CRMProjectRepositoryException;
    List<User> selectAllUsersByTaskId(Integer taskId) throws CRMProjectRepositoryException;
    List<User> selectAllUsersByRoleId(Integer roleId) throws CRMProjectRepositoryException;
    User add(User user) throws CRMProjectRepositoryException;
    User update(User user) throws CRMProjectRepositoryException;  //update user by id
    void remove(Integer userId) throws CRMProjectRepositoryException;

}







