package com.itrex.java.lab.projectcrmspringboot.repository;

import com.itrex.java.lab.projectcrmspringboot.entity.User;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractUserRepositoryTest extends BaseRepositoryTest {

    private UserRepository repository;

    public AbstractUserRepositoryTest() {
        super();
    }

    public void postConstruct(UserRepository repository) {
        this.repository = repository;
    }

    @Test
    public void selectAll_validData_returnExistUserTest() throws CRMProjectRepositoryException {
        //given && when
        final List<User> result = repository.selectAll();

        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void selectAll_shouldThrowRepositoryExceptionTest() {
        //given && when
        cleanDB();    //clean Data Base

        //then
        assertThrows(CRMProjectRepositoryException.class, () -> repository.selectAll());
    }

    @Test
    void selectById_validData_existUserId_returnUserTest() throws CRMProjectRepositoryException {
        //given
        Integer idUser = 2;

        //when
        User actual = repository.selectById(idUser);

        //then
        assertEquals(2, actual.getId());
        assertEquals("Ivanov", actual.getLogin());
        assertEquals("123", actual.getPsw());
        assertEquals("ADMIN", actual.getRole().getRoleName());
        assertEquals("Ivanov", actual.getLastName());
        assertEquals("Ivan", actual.getFirstName());
    }

    @Test
    void selectById_validData_existIDUsereNotDataBase_returnNULLTest() throws CRMProjectRepositoryException {
        //given
        Integer idUser = 28;

        //when
        User actual = repository.selectById(idUser);

        //then
        assertNull(actual);
    }

    @Test
    void selectById_shouldThrowRepositoryExceptionTest() {
        //given && when
        cleanDB();    //clean Data Base
        Integer idUser = 2;

        //then
        assertThrows(CRMProjectRepositoryException.class, () -> repository.selectById(idUser));
    }

    @Test
    void selectAllUsersByTask_existTask_returnListOfUserTest() throws CRMProjectRepositoryException {
        //given
        Integer idTask = 2;

        //when
        List<User> actual = repository.selectAllUsersByTaskId(idTask);

        //then
        assertEquals(1, actual.get(0).getId());
        assertEquals("Petrov", actual.get(0).getLogin());
        assertEquals("123", actual.get(0).getPsw());
        assertEquals("ADMIN", actual.get(0).getRole().getRoleName());
        assertEquals("Petrov", actual.get(0).getLastName());
        assertEquals("Petr", actual.get(0).getFirstName());
        assertEquals(8, actual.get(1).getId());
        assertEquals("Dropalo", actual.get(1).getLogin());
        assertEquals("123", actual.get(1).getPsw());
        assertEquals("USER", actual.get(1).getRole().getRoleName());
        assertEquals("Dropalo", actual.get(1).getLastName());
        assertEquals("Andrey", actual.get(1).getFirstName());
    }

    @Test
    void add_validData_existUser_returnUserTest() throws CRMProjectRepositoryException {
        //given
        User user = RepositoryTestUtils.createTestUsers(1).get(0);

        //when
        User actual = repository.add(user);

        //then
        assertEquals(11, actual.getId());
        assertEquals("Test 0", actual.getLogin());
        assertEquals("1230", actual.getPsw());
        assertEquals("ADMIN", actual.getRole().getRoleName());
        assertEquals("Ivanov 0", actual.getLastName());
        assertEquals("Ivan 0", actual.getFirstName());
    }

    @Test
    void add_existCopyUSERId2_returnThrowRepositoryExceptionTest() throws CRMProjectRepositoryException {
        //given && when
        User user = repository.selectById(2);  //There is Role "User" in Data Base

        //then
        assertThrows(CRMProjectRepositoryException.class, () -> repository.add(user));
    }

    @Test
    void update_validData_existUser_returnUserTest() throws CRMProjectRepositoryException {
        //given
        User expected = RepositoryTestUtils.createTestUsers(1).get(0);
        expected.setId(1);

        //when
        User actual = repository.update(expected);

        //then
        assertEquals(1, actual.getId());
        assertEquals("Test 0", actual.getLogin());
        assertEquals("1230", actual.getPsw());
        assertEquals("ADMIN", actual.getRole().getRoleName());
        assertEquals("Ivanov 0", actual.getLastName());
        assertEquals("Ivan 0", actual.getFirstName());
    }

    @Test
    void remove_validData_existUser_Test() throws CRMProjectRepositoryException {
        //given
        User user = repository.selectById(1);

        //when
        repository.remove(1);

        //then
        assertEquals(9, repository.selectAll().size());
    }

    @Test
    void remove_existUserId_shouldThrowRepositoryExceptionTest() {
        //given && when
        cleanDB();    //clean Data Base

        //then
        assertThrows(CRMProjectRepositoryException.class, () -> repository.remove(1));
    }

    @Test
    void remove_existUserIdNoDataBase_shouldThrowRepositoryExceptionTest() {
        //given && when
        int idUserNoDB = 18;

        //then
        assertThrows(CRMProjectRepositoryException.class, () -> repository.remove(idUserNoDB));
    }

    @Test
    void selectAllUsersByRoleId_existRoleId_returnListOfUsersTest() throws CRMProjectRepositoryException {
        //given
        Integer roleId = 1;

        //when
        List<User> users = repository.selectAllUsersByRoleId(roleId);

        //then
        assertEquals(3, users.size());
        assertEquals(1, users.get(0).getId());
        assertEquals(2, users.get(1).getId());
        assertEquals(3, users.get(2).getId());
    }

    @Test
    void selectAllUsersByRoleId_existRoleIdNoDB_returnEmptyListTest() throws CRMProjectRepositoryException {
        //given
        Integer roleId = 5;

        //when
        List<User> users = repository.selectAllUsersByRoleId(roleId);

        //then
        assertEquals(0, users.size());
    }

}

