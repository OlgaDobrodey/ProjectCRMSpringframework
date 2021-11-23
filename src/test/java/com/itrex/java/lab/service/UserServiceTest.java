package com.itrex.java.lab.service;

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
import com.itrex.java.lab.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.itrex.java.lab.repository.RepositoryTestUtils.*;
import static com.itrex.java.lab.utils.ConverterUtils.convertUserToDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private RoleRepository roleRepository;

    @Test
    public void getAll_returnUserDTOTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given && when
        List<User> result = createTestUsersWithId(0, 2);
        when(userRepository.selectAll()).thenReturn(result);

        //then
        assertFalse(userService.getAll().isEmpty());
        assertEquals(userService.getAll().size(), 2);
        verify(userRepository, Mockito.times(2)).selectAll();
    }

    @Test
    void getAll_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        when(userRepository.selectAll()).thenThrow(CRMProjectRepositoryException.class);

        //then
        assertThrows(CRMProjectServiceException.class, () -> userService.getAll());
        verify(userRepository).selectAll();
    }

    @Test
    void getById_existUserDTOId_returnUserDTOTest()
            throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Integer idUser = 2;
        User user = createTestUsersWithId(1, 1).get(0);
        when(userRepository.selectById(idUser)).thenReturn(user);

        //when
        UserDTO actual = userService.getById(idUser);

        //then
        assertEquals(2, actual.getId());
        assertEquals("Test 1", actual.getLogin());
        assertEquals("1231", actual.getPsw());
        assertEquals(1, actual.getRoleId());
        assertEquals("Ivanov 1", actual.getLastName());
        assertEquals("Ivan 1", actual.getFirstName());
        verify(userRepository).selectById(idUser);
    }

    @Test
    void getById_existIDUsereDTONotDataBase_returnNULLTest()
            throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Integer idUser = 28;
        when(userRepository.selectById(idUser)).thenReturn(null);

        //when
        UserDTO actual = userService.getById(idUser);

        //then
        assertNull(actual);
        verify(userRepository).selectById(idUser);
    }

    @Test
    void getById_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        Integer idUser = 2;
        when(userRepository.selectById(idUser)).thenThrow(CRMProjectRepositoryException.class);

        //then
        assertThrows(CRMProjectServiceException.class, () -> userService.getById(idUser));
        verify(userRepository).selectById(idUser);
    }

    @Test
    void getAllUsersByTaskDTO_existTaskDTO_returnListOfUserDTOTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Task task = createTestTasks(1).get(0);
        task.setId(1);
        List<User> users = createTestUsers(2);
        users.get(0).setId(1);
        users.get(1).setId(2);
        when(taskRepository.selectById(task.getId())).thenReturn(task);
        when(userRepository.selectAllUsersByTaskId(task.getId())).thenReturn(users);

        //when
        List<UserDTO> actual = userService.getAllUsersByTaskId(task.getId());

        //then
        assertEquals(1, actual.get(0).getId());
        assertEquals("Test 0", actual.get(0).getLogin());
        assertEquals("1230", actual.get(0).getPsw());
        assertEquals(1, actual.get(0).getRoleId());
        assertEquals("Ivanov 0", actual.get(0).getLastName());
        assertEquals("Ivan 0", actual.get(0).getFirstName());
        assertEquals(2, actual.get(1).getId());
        assertEquals("Test 1", actual.get(1).getLogin());
        assertEquals("1231", actual.get(1).getPsw());
        assertEquals(1, actual.get(1).getRoleId());
        assertEquals("Ivanov 1", actual.get(1).getLastName());
        assertEquals("Ivan 1", actual.get(1).getFirstName());
        verify(userRepository).selectAllUsersByTaskId(task.getId());
        verify(taskRepository).selectById(task.getId());
    }

    @Test
    void getAllUsersFromRoleId_existRoleId_returnListUsersTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given && when
        Role role = Role.builder().users(new ArrayList<>()).build();
        List<User> users = createTestUsersWithId(0,2);
        List<UserDTO> userDTOS = users.stream().map(user -> convertUserToDto(user)).collect(Collectors.toList());
        when(roleRepository.selectById(any())).thenReturn(role);
        when(userRepository.selectAllUsersByRoleId(role.getId())).thenReturn(users);

        //then
        assertEquals(userDTOS, userService.getAllUsersByRoleId(role.getId()));
        verify(roleRepository).selectById(any());
        verify(userRepository).selectAllUsersByRoleId(role.getId());
    }

    @Test
    void getAllUsersFromRoleId_existRoleIdNoDB_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given && when
        Role role = Role.builder().users(new ArrayList<>()).build();
        when(roleRepository.selectById(any())).thenReturn(null);

        //then
        assertThrows(CRMProjectServiceException.class, () -> userService.getAllUsersByRoleId(any()));
        verify(roleRepository).selectById(any());
    }


    @Test
    void add_validData_existUser_returnUserTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        User user = createTestUsers(1).get(0);
        User returnUser = createTestUsersWithId(0, 1).get(0);
        when(roleRepository.selectById(user.getRole().getId())).thenReturn(user.getRole());
        when(userRepository.add(user)).thenReturn(returnUser);

        //when
        UserDTO actual = userService.add(convertUserToDto(user));

        //then
        assertEquals(1, actual.getId());
        assertEquals("Test 0", actual.getLogin());
        assertEquals("1230", actual.getPsw());
        assertEquals(1, actual.getRoleId());
        assertEquals("Ivanov 0", actual.getLastName());
        assertEquals("Ivan 0", actual.getFirstName());
        verify(userRepository).add(user);
        verify(roleRepository).selectById(1);
    }

    @Test
    void add_existCopyUSERId2_returnThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        User user = createTestUsers(1).get(0);
        when(roleRepository.selectById(any())).thenReturn(user.getRole());
        when(userRepository.add(user)).thenThrow(CRMProjectRepositoryException.class);
        //There is Role "User" in Data Base

        //then
        assertThrows(CRMProjectServiceException.class, () -> userService.add(convertUserToDto(user)));
        verify(userRepository).add(user);
        verify(roleRepository).selectById(any());
    }

    @Test
    void update_validData_existUser_returnUserTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        User expected = createTestUsersWithId(0, 1).get(0);
        when(userRepository.selectById(1)).thenReturn(expected);
        when(roleRepository.selectById(1)).thenReturn(Role.builder().id(1).roleName("ADMIN").build());
        when(userRepository.update(expected)).thenReturn(expected);

        //when
        UserDTO actual = userService.update(convertUserToDto(expected));

        //then
        assertEquals(1, actual.getId());
        assertEquals("Test 0", actual.getLogin());
        assertEquals("1230", actual.getPsw());
        assertEquals(1, actual.getRoleId());
        assertEquals("Ivanov 0", actual.getLastName());
        assertEquals("Ivan 0", actual.getFirstName());
        verify(userRepository).update(expected);
        verify(userRepository).selectById(any());
        verify(roleRepository).selectById(any());
    }

    @Test
    void assignTaskFromUser_existIdTaskAndIdUserTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        User user = createTestUsersWithId(1, 1).get(0);
        Task task = createTestTasksWithId(1, 1).get(0);
        user.setTasks(new ArrayList<>());
        when(userRepository.selectById(user.getId())).thenReturn(user);
        when(taskRepository.selectById(task.getId())).thenReturn(task);

        //when
        userService.assignTaskToUser(task.getId(), user.getId());

        //then
        verify(userRepository).selectById(any());
        verify(taskRepository).selectById(any());
    }

    @Test
    void update_validData_existUserNotDB_returnNullTest() throws CRMProjectRepositoryException {
        //given && when
        User expected = createTestUsersWithId(0, 1).get(0);
        when(userRepository.selectById(1)).thenReturn(null);

        //then
        assertThrows(CRMProjectServiceException.class, () -> userService.update(convertUserToDto(expected)));
        verify(userRepository).selectById(any());
    }

    @Test
    void update_existUser_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        User user = createTestUsersWithId(0, 1).get(0);
        when(roleRepository.selectById(1)).thenReturn(null);
        when(userRepository.selectById(1)).thenReturn(user);

        //then
        assertThrows(CRMProjectServiceException.class, () -> userService.update(convertUserToDto(user)));
        verify(userRepository).selectById(1);
        verify(roleRepository).selectById(1);
    }

    @Test
    void remove_existUserDTO_shouldReturnExistTrueTest()
            throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        User user = createTestUsersWithId(1, 2).get(0);
        when(userRepository.selectById(user.getId())).thenReturn(user);
        doNothing().when(userRepository).remove(user.getId());
        //when
        userService.remove(user.getId());

        //then
        verify(userRepository).remove(user.getId());
        verify(userRepository).selectById(any());
    }

    @Test
    void revokeTaskFromUserId_existUserIdAndTaskIdTest()
            throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Task task = createTestTasksWithId(1, 1).get(0);
        User user = createTestUsersWithId(1, 1).get(0);
        user.setTasks(new ArrayList<>(List.of(task)));
        task.setUsers(new ArrayList<>(List.of(user)));
        when(userRepository.selectById(user.getId())).thenReturn(user);
        when(taskRepository.selectById(task.getId())).thenReturn(task);

        // when
        userService.revokeTaskFromUser(task.getId(), user.getId());

        //then
        assertEquals(Status.DONE, task.getStatus());
        verify(userRepository).selectById(user.getId());
        verify(taskRepository).selectById(task.getId());
    }

    @Test
    void revokeTaskFromUserId_existUserIdAndTaskIdHadMoreOneUsersTest()
            throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        Task task = createTestTasksWithId(1, 1).get(0);
        List<User> users = createTestUsersWithId(1, 3);
        users.forEach(u -> u.setTasks(new ArrayList<>(List.of(task))));
        task.setUsers(users);
        when(userRepository.selectById(users.get(0).getId())).thenReturn(users.get(0));
        when(taskRepository.selectById(task.getId())).thenReturn(task);

        // when
        userService.revokeTaskFromUser(task.getId(), users.get(0).getId());

        //then
        assertEquals(Status.NEW, task.getStatus());
        verify(userRepository).selectById(users.get(0).getId());
        verify(taskRepository).selectById(task.getId());
    }

    @Test
    void revokeTaskFromUserId_existUserIdAndTaskIdNoDBTest()
            throws CRMProjectRepositoryException {
        //given
        Task task = createTestTasksWithId(1, 1).get(0);
        User user = createTestUsersWithId(1, 1).get(0);
        when(taskRepository.selectById(task.getId())).thenReturn(null);

        // when
        assertThrows(CRMProjectServiceException.class, () -> userService.revokeTaskFromUser(task.getId(), user.getId()));

        //then
        verify(taskRepository).selectById(task.getId());
    }

    @Test
    void remove_existUserId_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        User user = createTestUsersWithId(1, 2).get(0);
        when(userRepository.selectById(user.getId())).thenReturn(user);
        doThrow(CRMProjectRepositoryException.class).when(userRepository).remove(user.getId());    //clean Data Base

        //then
        assertThrows(CRMProjectServiceException.class, () -> userService.remove(user.getId()));
        verify(userRepository).remove(user.getId());
        verify(userRepository).selectById(user.getId());
    }

    @Test
    void revokeAllUserTasksByUserId_existIdUserTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        User user = createTestUsersWithId(1, 2).get(0);
        when(userRepository.selectById(user.getId())).thenReturn(user);

        //when
        userService.revokeAllUserTasksByUserId(2);

        //then
        verify(userRepository).selectById(any());
    }

    @Test
    void updateUserPassword_exitPasswordDTOAndUserId_returnUserDTOTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given
        User user = createTestUsersWithId(0, 1).get(0);
        when(userRepository.selectById(user.getId())).thenReturn(user);
        PasswordDTOForChanges pdtc = PasswordDTOForChanges.builder()
                .newPassword("12345")
                .oldPassword("1230")
                .build();
        when(userRepository.update(user)).thenReturn(user);

        //when
        UserDTO expected = userService.updateUserPassword(pdtc, user.getId());

        //then
        assertEquals(1, expected.getId());
        assertEquals("Test 0", expected.getLogin());
        assertEquals("12345", expected.getPsw());
        assertEquals(1, expected.getRoleId());
        assertEquals("Ivanov 0", expected.getLastName());
        assertEquals("Ivan 0", expected.getFirstName());
        verify(userRepository).selectById(user.getId());
        verify(userRepository).update(user);
    }

    @Test
    void updateUserPassword_exitPasswordDTOEqualsNullAndUserIdEqualsNull_shouldThrowServiceExceptionTest() throws CRMProjectRepositoryException, CRMProjectServiceException {
        //given && when
        User user = createTestUsersWithId(0, 1).get(0);
        Integer userIdNoDB = 5;
        when(userRepository.selectById(user.getId())).thenReturn(user);
        when(userRepository.selectById(userIdNoDB)).thenReturn(null);
        PasswordDTOForChanges pdtcNewPswEmpty = PasswordDTOForChanges.builder()
                .newPassword("")
                .oldPassword("1230")
                .build();

        PasswordDTOForChanges pdtcOldPswEmpty = PasswordDTOForChanges.builder()
                .newPassword("1235")
                .oldPassword(" ")
                .build();

        PasswordDTOForChanges pdtc = PasswordDTOForChanges.builder()
                .newPassword("12345")
                .oldPassword("1230")
                .build();

        //then
        assertThrows(CRMProjectServiceException.class,
                () -> userService.updateUserPassword(pdtcNewPswEmpty, user.getId()));
        assertThrows(CRMProjectServiceException.class,
                () -> userService.updateUserPassword(pdtcOldPswEmpty, user.getId()));
        assertThrows(CRMProjectServiceException.class,
                () -> userService.updateUserPassword(pdtc, userIdNoDB));
        verify(userRepository).selectById(user.getId());
        verify(userRepository).selectById(userIdNoDB);
    }

}

