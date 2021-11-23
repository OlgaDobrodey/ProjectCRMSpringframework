package com.itrex.java.lab.service;

import com.itrex.java.lab.dto.PasswordDTOForChanges;
import com.itrex.java.lab.dto.UserDTO;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.exceptions.CRMProjectServiceException;

import java.util.List;

public interface UserService {

    List<UserDTO> getAll() throws CRMProjectServiceException;
    UserDTO getById(Integer id) throws CRMProjectServiceException;
    List<UserDTO> getAllUsersByTaskId(Integer taskId) throws CRMProjectServiceException;
    List<UserDTO> getAllUsersByRoleId(Integer roleId) throws CRMProjectRepositoryException, CRMProjectServiceException;

    UserDTO add(UserDTO user) throws CRMProjectServiceException;
    void assignTaskToUser(Integer taskId, Integer userId) throws CRMProjectServiceException; //add task for user

    UserDTO update(UserDTO user) throws CRMProjectServiceException; //update user everything except the psw
    UserDTO updateUserPassword(PasswordDTOForChanges passwordDTO, Integer userId) throws CRMProjectRepositoryException, CRMProjectServiceException;

    void remove(Integer userId) throws CRMProjectServiceException;
    void revokeTaskFromUser(Integer taskId, Integer userId) throws CRMProjectServiceException;
    void revokeAllUserTasksByUserId(Integer userId) throws CRMProjectServiceException;

}
