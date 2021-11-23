package com.itrex.java.lab.service;

import com.itrex.java.lab.dto.RoleDTO;
import com.itrex.java.lab.entity.Role;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.exceptions.CRMProjectServiceException;
import com.itrex.java.lab.repository.RepositoryTestUtils;
import com.itrex.java.lab.repository.RoleRepository;
import com.itrex.java.lab.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static com.itrex.java.lab.utils.ConverterUtils.convertRoleToDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;
    @Mock
    private RoleRepository roleRepository;

    @Test
    void getAllRole_returnRoleDTOTest() throws CRMProjectServiceException, CRMProjectRepositoryException {
        //given && when
        Mockito.when(roleRepository.selectAll()).thenReturn(new ArrayList<>());

        //then
        assertEquals(roleService.getAllRoles().size(), 0);
        Mockito.verify(roleRepository).selectAll();
    }

    @Test
    void selectAll_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        Mockito.when(roleRepository.selectAll()).thenThrow(CRMProjectRepositoryException.class);

        //then
        assertThrows(CRMProjectServiceException.class, () -> roleService.getAllRoles());
        Mockito.verify(roleRepository, Mockito.times(1)).selectAll();
    }

    @Test
    void getById_existIDRoleDTO_returnUSERRoleTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Integer idRole = 2;
        Role role = new Role();
        role.setId(2);
        role.setRoleName("USER");
        Mockito.when(roleRepository.selectById(2)).thenReturn(role);

        //when
        RoleDTO actual = roleService.getById(idRole);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).selectById(any());
        assertEquals(2, actual.getId());
        assertEquals("USER", actual.getRoleName());
    }

    @Test
    void getById_existIDRoleDTONotDataBase_returnNullTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Integer roleId = 28;
        Mockito.when(roleRepository.selectById(roleId)).thenReturn(null);
        //when
        RoleDTO actual = roleService.getById(roleId);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).selectById(any());
        assertNull(actual);
    }

    @Test
    void getById_existRoleDTOId_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        Mockito.when(roleRepository.selectById(any())).thenThrow(CRMProjectRepositoryException.class);

        //then
        assertThrows(CRMProjectServiceException.class, () -> roleService.getById(any()));
        Mockito.verify(roleRepository, Mockito.times(1)).selectById(any());
    }

    @Test
    void addRole_existRoleDTO_returnRoleDTOTest() throws CRMProjectServiceException, CRMProjectRepositoryException {
        //given && when
        Role testRole = RepositoryTestUtils.createTestRole(1).get(0);
        Mockito.when(roleRepository.add(testRole)).thenReturn(testRole);

        //then
        assertEquals("TEST " + 0, roleService.addRole(convertRoleToDto(testRole)).getRoleName());
        Mockito.verify(roleRepository, Mockito.times(1)).add(any());
    }

    @Test
    void updateRole_existRoleDTO_returnRoleTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Role expected = RepositoryTestUtils.createTestRole(1).get(0);
        Integer testId = 1;
        expected.setId(testId);
        Mockito.when(roleRepository.selectById(1)).thenReturn(expected);
        Mockito.when(roleRepository.update(expected)).thenReturn(expected);

        //when
        RoleDTO actual = roleService.updateRole(convertRoleToDto(expected));

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).update(any());
        Mockito.verify(roleRepository).selectById(any());
        assertEquals(1, actual.getId());
        assertEquals("TEST 0", actual.getRoleName());
    }

    @Test
    void updateRole_existRoleDTONonDB_returnNullTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given && when
        Role expected = RepositoryTestUtils.createTestRole(1).get(0);
        expected.setId(99);
        Mockito.when(roleRepository.selectById(expected.getId())).thenReturn(null);

        //when
        assertThrows(CRMProjectServiceException.class, () -> roleService.updateRole(convertRoleToDto(expected)));

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).selectById(any());
    }

    @Test
    void update_existRoleDTO_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        Role role = new Role();
        role.setId(1);
        role.setRoleName("Test Role");
        Mockito.when(roleRepository.selectById(1)).thenReturn(role);
        Mockito.when(roleRepository.update(role)).thenThrow(CRMProjectRepositoryException.class);

        //then
        assertThrows(CRMProjectServiceException.class, () -> roleService.updateRole(convertRoleToDto(role)));
        Mockito.verify(roleRepository, Mockito.times(1)).update(any());
        Mockito.verify(roleRepository).selectById(any());
    }

}