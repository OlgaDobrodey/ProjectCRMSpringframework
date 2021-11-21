package com.itrex.java.lab.projectcrmspringboot.service;


import com.itrex.java.lab.projectcrmspringboot.dto.TaskDTO;
import com.itrex.java.lab.projectcrmspringboot.entity.Status;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectServiceException;

import java.util.List;

public interface TaskService {

    List<TaskDTO> getAll() throws CRMProjectServiceException;

    TaskDTO getById(Integer id) throws CRMProjectServiceException;

    List<TaskDTO> getAllTasksByUserId(Integer userId) throws CRMProjectServiceException;

    TaskDTO add(TaskDTO task) throws CRMProjectServiceException;

    TaskDTO update(TaskDTO task) throws CRMProjectServiceException;

    void finishTaskByTaskId(Integer taskId) throws CRMProjectServiceException; //delete all users for task

    void remove(Integer taskId) throws CRMProjectServiceException;

    TaskDTO changePasswordDTO(Status status, Integer taskId) throws CRMProjectServiceException;

}
