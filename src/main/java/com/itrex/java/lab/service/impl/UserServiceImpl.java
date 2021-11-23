package com.itrex.java.lab.service.impl;

import com.itrex.java.lab.dto.PasswordDTOForChanges;
import com.itrex.java.lab.dto.UserDTO;
import com.itrex.java.lab.entity.Role;
import com.itrex.java.lab.entity.Status;
import com.itrex.java.lab.entity.Task;
import com.itrex.java.lab.entity.User;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.exceptions.CRMProjectServiceException;
import com.itrex.java.lab.repository.RoleRepository;
import com.itrex.java.lab.repository.TaskRepository;
import com.itrex.java.lab.repository.UserRepository;
import com.itrex.java.lab.service.UserService;
import com.itrex.java.lab.utils.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.itrex.java.lab.utils.ConverterUtils.convertUserToDto;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TaskRepository taskRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<UserDTO> getAll() throws CRMProjectServiceException {
        try {
            return userRepository.selectAll().stream()
                    .map(ConverterUtils::convertUserToDto)
                    .collect(Collectors.toList());
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: GET ALL USER:", ex);
        }
    }

    @Override
    public UserDTO getById(Integer id) throws CRMProjectServiceException {
        try {
            User user = userRepository.selectById(id);
            return user != null ? convertUserToDto(user) : null;
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: SELECT USER BY ID: ", ex);
        }
    }

    @Override
    public List<UserDTO> getAllUsersByTaskId(Integer taskId) throws CRMProjectServiceException {
        try {
            Task task = taskRepository.selectById(taskId);
            if(task == null){
                throw new CRMProjectServiceException("ERROR SERVICE: NO FOUND TASK WITH ID");
            }
            return userRepository.selectAllUsersByTaskId(taskId)
                    .stream().map(ConverterUtils::convertUserToDto)
                    .collect(Collectors.toList());
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: GET ALL USER BY TASK:", ex);
        }
    }

    @Override
    @Transactional
    public List<UserDTO> getAllUsersByRoleId(Integer roleId) throws CRMProjectServiceException {
        try {
            Role role = roleRepository.selectById(roleId);
            if (role == null) {
                throw new CRMProjectServiceException("ERROR SERVICE: NO ROLE FOUND WITH ID");
            }
            return userRepository.selectAllUsersByRoleId(roleId)
                    .stream()
                    .map(ConverterUtils::convertUserToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CRMProjectServiceException("ERROR SERVICE: GET ALL USERS FROM ROLE:", e);
        }
    }

    @Override
    public UserDTO add(UserDTO userDTO) throws CRMProjectServiceException {
        try {
            User user = User.builder()
                    .login(userDTO.getLogin())
                    .psw(userDTO.getPsw())
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastName())
                    .role(roleRepository.selectById(userDTO.getRoleId()))
                    .build();
            return convertUserToDto(userRepository.add(user));
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: ADD USER:", ex);
        }
    }

    @Override
    @Transactional
    public void assignTaskToUser(Integer taskId, Integer userId) throws CRMProjectServiceException {
        try {
            Task task = taskRepository.selectById(taskId);
            User user = userRepository.selectById(userId);
            user.getTasks().add(task);
            userRepository.update(user);
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: ADD TASK BY USER:", ex);
        }
    }

    @Override
    public UserDTO update(UserDTO userDTO) throws CRMProjectServiceException {
        try {
            User user = userRepository.selectById(userDTO.getId());
            Role role = roleRepository.selectById(userDTO.getRoleId());
            if (user == null || role == null) {
                throw new CRMProjectServiceException("ERROR SERVICE: UPDATE USER: USER BY ID "
                        + userDTO.getId() + " NO FOUND DATA BASE");
            }
            if (!checkIfValidPassword(user, userDTO.getPsw())) {
                throw new CRMProjectServiceException("ERROR SERVICE: Wrong password");
            }
            user.setLogin(userDTO.getLogin());
            user.setLastName(userDTO.getLastName());
            user.setFirstName(userDTO.getFirstName());
            user.setRole(role);
            return convertUserToDto(userRepository.update(user));
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: UPDATE USER:", ex);
        }
    }

    @Override
    @Transactional
    public UserDTO updateUserPassword(PasswordDTOForChanges passwordDTO, Integer userId) throws CRMProjectRepositoryException, CRMProjectServiceException {
        try {
            if (passwordDTO.getNewPassword().isBlank()) {
                throw new CRMProjectServiceException("ERROR SERVICE updateUserPassword: newPassword is empty ");
            }

            User user = userRepository.selectById(userId);

            if (!checkIfValidPassword(user, passwordDTO.getOldPassword())) {
                throw new CRMProjectServiceException("ERROR SERVICE updateUserPassword: no found on DB user by oldPassword");
            }
            user.setPsw(passwordDTO.getNewPassword());
            return convertUserToDto(userRepository.update(user));
        } catch (CRMProjectRepositoryException e) {
            throw new CRMProjectServiceException("ERROR SERVICE: updateUserPassword:", e);
        }
    }

    @Transactional
    @Override
    public void remove(Integer userId) throws CRMProjectServiceException {
        try {
            User user = userRepository.selectById(userId);
            if (user == null) {
                throw new CRMProjectServiceException("ERROR SERVICE: DELETE USER: no found Data BASE");
            }
            userRepository.remove(userId);
            userRepository.update(user);
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: DELETE USER:", ex);
        }
    }

    @Transactional
    @Override
    public void revokeTaskFromUser(Integer taskId, Integer userId) throws CRMProjectServiceException {
        try {
            Task task = taskRepository.selectById(taskId);
            if (task == null) {
                throw new CRMProjectServiceException("ERROR SERVICE: DELETE TASK BY USER: task == null");
            }

            List<User> users = task.getUsers();
            if (users.size() == 1) {
                if (users.get(0).getTasks().get(0).getId().equals(taskId))
                    task.setStatus(Status.DONE);
            }

            User user = userRepository.selectById(userId);
            if (user == null) {
                throw new CRMProjectServiceException("ERROR SERVICE: DELETE TASK BY USER: user == null");
            }
            user.getTasks().remove(task);
            userRepository.update(user);
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: DELETE TASK BY USER:", ex);
        }
    }

    @Override
    @Transactional
    public void revokeAllUserTasksByUserId(Integer userId) throws CRMProjectServiceException {
        try {
            User user = userRepository.selectById(userId);
            user.setTasks(new ArrayList<>());
            userRepository.update(user);
        } catch (CRMProjectRepositoryException ex) {
            throw new CRMProjectServiceException("ERROR SERVICE: DELETE ALL TASKS BY USER:", ex);
        }
    }

    private boolean checkIfValidPassword(User user, String password) {
        if (user == null || password == null) {
            return false;
        }
        return user.getPsw().equals(password);
    }

}
