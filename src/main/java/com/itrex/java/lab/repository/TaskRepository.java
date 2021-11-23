package com.itrex.java.lab.repository;

import com.itrex.java.lab.entity.Task;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;

import java.util.List;

public interface TaskRepository {

    List<Task> selectAll() throws CRMProjectRepositoryException;
    Task selectById(Integer id) throws CRMProjectRepositoryException;
    List<Task> selectAllTasksByUserId(Integer idUser) throws CRMProjectRepositoryException;
    Task add(Task task) throws CRMProjectRepositoryException;
    Task update(Task task) throws CRMProjectRepositoryException;
    void remove(Integer taskId) throws CRMProjectRepositoryException;

}
