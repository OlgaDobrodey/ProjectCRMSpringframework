package com.itrex.java.lab.projectcrmspringboot.repository;

import com.itrex.java.lab.projectcrmspringboot.entity.Task;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;

import java.util.List;

public interface TaskRepository {

    List<Task> selectAll() throws CRMProjectRepositoryException;

    Task selectById(Integer id) throws CRMProjectRepositoryException;
    Task selectByIdWithAllTaskUsers(Integer id) throws CRMProjectRepositoryException;

    List<Task> selectAllTasksByUserId(Integer idUser) throws CRMProjectRepositoryException;

    Task add(Task task) throws CRMProjectRepositoryException;

    Task update(Task task) throws CRMProjectRepositoryException;

    void remove(Integer taskId) throws CRMProjectRepositoryException;

}
