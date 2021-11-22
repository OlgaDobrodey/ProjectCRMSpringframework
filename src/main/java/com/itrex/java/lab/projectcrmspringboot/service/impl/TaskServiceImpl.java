package com.itrex.java.lab.projectcrmspringboot.service.impl;

import com.itrex.java.lab.projectcrmspringboot.dto.TaskDTO;
import com.itrex.java.lab.projectcrmspringboot.entity.Status;
import com.itrex.java.lab.projectcrmspringboot.entity.Task;
import com.itrex.java.lab.projectcrmspringboot.entity.User;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectServiceException;
import com.itrex.java.lab.projectcrmspringboot.repository.TaskRepository;
import com.itrex.java.lab.projectcrmspringboot.repository.UserRepository;
import com.itrex.java.lab.projectcrmspringboot.service.TaskService;
import com.itrex.java.lab.projectcrmspringboot.utils.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.itrex.java.lab.projectcrmspringboot.utils.ConverterUtils.convertTaskToDto;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public List<TaskDTO> getAll() throws CRMProjectServiceException {
        try {
            return taskRepository.selectAll().stream()
                    .map(ConverterUtils::convertTaskToDto)
                    .collect(Collectors.toList());
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: GET ALL TASK:", ex);
        }
    }

    @Override
    public TaskDTO getById(Integer id) throws CRMProjectServiceException {
        try {
            Task task = taskRepository.selectById(id);
            return task != null ? convertTaskToDto(task) : null;
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: GET BY ID TASK:", ex);
        }
    }

    @Override
    public List<TaskDTO> getAllTasksByUserId(Integer userId) throws CRMProjectServiceException {
        try {
            return taskRepository.selectAllTasksByUserId(userId).stream()
                    .map(ConverterUtils::convertTaskToDto)
                    .collect(Collectors.toList());
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: SELECT TASK BY USER: ", ex);
        }
    }

    @Override
    @Transactional
    public TaskDTO add(TaskDTO task) throws CRMProjectServiceException {
        try {
            Task addTask = Task.builder()
                    .title(task.getTitle())
                    .status(Status.NEW)
                    .deadline(task.getDeadline())
                    .info(task.getInfo())
                    .build();
            return convertTaskToDto(taskRepository.add(addTask));
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: ADD TASK:", ex);
        }
    }

    @Override
    @Transactional
    public TaskDTO update(TaskDTO task) throws CRMProjectServiceException {
        try {
            Task update = taskRepository.selectById(task.getId());
            if (update == null) {
                throw new CRMProjectServiceException("TASK NO FOUND DATA BASE");
            }
            update.setTitle(task.getTitle());
            update.setStatus(task.getStatus());
            update.setDeadline(task.getDeadline());
            update.setInfo(task.getInfo());
            return convertTaskToDto(taskRepository.update(update));
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: UPDATE TASK:", ex);
        }
    }

    @Override
    @Transactional
    public void finishTaskByTaskId(Integer taskId) throws CRMProjectServiceException {
        try {
            Task task = taskRepository.selectByIdWithAllTaskUsers(taskId);
            task.setStatus(Status.DONE);

            List<User> users = task.getUsers();
            for (User user : users) {
                userRepository.selectByIdWithAllUserTasks(user.getId()).getTasks().removeIf(t -> t.getId() == taskId);
            }
            taskRepository.update(task);
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: DELETE ALL TASK:", ex);
        }
    }

    @Override
    @Transactional
    public void remove(Integer taskId) throws CRMProjectServiceException {
        try {
            Task task = taskRepository.selectById(taskId);
            task.setStatus(Status.DONE);

            task.getUsers().forEach(user -> user.getTasks().removeIf(t -> t.getId() == taskId));
            taskRepository.remove(taskId);
            taskRepository.update(task);
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: DELETE TASK:", ex);
        }
    }

    @Override
    @Transactional
    public TaskDTO changePasswordDTO(Status status, Integer taskId) throws CRMProjectServiceException {
        try {
            Task task = taskRepository.selectById(taskId);
            if (task == null) {
                throw new CRMProjectServiceException("TASK SERVICE: changeStatusForTaskId: no found task by Id" + taskId);
            }
            if (status == Status.PROGRESS && CollectionUtils.isEmpty(task.getUsers())) {
                throw new CRMProjectServiceException("Wrong status: " + status);
            } else if (status != Status.PROGRESS && !CollectionUtils.isEmpty(task.getUsers())) {
                task.getUsers().forEach(user -> user.getTasks().removeIf(t -> t.getId() == taskId));
            }
            task.setStatus(status);
            return convertTaskToDto(taskRepository.update(task));
        } catch (
                CRMProjectRepositoryException e) {
            throw new CRMProjectServiceException("TASK SERVICE: changeStatusForTaskId:", e);
        }
    }

}
