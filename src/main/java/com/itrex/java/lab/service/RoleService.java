package com.itrex.java.lab.service;

import com.itrex.java.lab.dto.RoleDTO;
import com.itrex.java.lab.exceptions.CRMProjectServiceException;

import java.util.List;

public interface RoleService {

    List<RoleDTO> getAllRoles() throws CRMProjectServiceException;
    RoleDTO getById(Integer id) throws CRMProjectServiceException;
    RoleDTO addRole(RoleDTO role) throws CRMProjectServiceException;
    RoleDTO updateRole(RoleDTO role) throws CRMProjectServiceException;

}
