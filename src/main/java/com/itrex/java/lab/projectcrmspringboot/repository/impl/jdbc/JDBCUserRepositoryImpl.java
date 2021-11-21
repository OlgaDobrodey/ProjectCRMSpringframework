package com.itrex.java.lab.projectcrmspringboot.repository.impl.jdbc;

import com.itrex.java.lab.projectcrmspringboot.entity.Role;
import com.itrex.java.lab.projectcrmspringboot.entity.User;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.projectcrmspringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Qualifier("JDBCUserRepository")
@Deprecated
public class JDBCUserRepositoryImpl implements UserRepository {

    private static final String ID_USER_COLUMN = "user.id";
    private static final String LOGIN_USER_COLUMN = "user.login";
    private static final String PSW_USER_COLUMN = "user.psw";
    private static final String ROLE_USER_COLUMN = "user.role_id";
    private static final String ROLE_NAME_TABLE_ROLE = "role.role_name";
    private static final String FIRST_NAME_USER_COLUMN = "user.first_name";
    private static final String LAST_NAME_USER_COLUMN = "user.last_name";
    private static final String CROSS_TABLE_ID_USER = "users_id";

    private static final String SELECT_ALL_QUERY =
            "SELECT user.id, user.login, user.psw, user.role_id, user.first_name, user.last_name, role.role_name " +
                    "FROM user join role on role.id = user.role_id";
    private static final String SELECT_USER_BY_ID_QUERY = SELECT_ALL_QUERY + " WHERE user.id = ";
    private static final String SELECT_ALL_USERS_FOR_TASK = "SELECT users_id FROM user_task WHERE tasks_id = ";
    private static final String SELECT_ALL_USERS_BY_ROLE = SELECT_ALL_QUERY + " WHERE user.role_id = ";

    private static final String INSERT_USER_QUERY = "INSERT INTO user(login, psw, role_id, first_name, last_name)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY =
            "UPDATE user SET user.login=?, user.psw=?, user.role_id=?, user.first_name=?, user.last_name=?" +
                    "  WHERE user.id = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM user WHERE user.id = ?";
    private static final String DELETE_USER_ALL_TASKS_QUERY = "DELETE FROM user_task WHERE users_id = ?";

    @Autowired
    private DataSource dataSource;

    @Override
    public List<User> selectAll() throws CRMProjectRepositoryException {
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_ALL_QUERY)) {

            while (resultSet.next()) {
                User user = getUser(resultSet);
                users.add(user);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS: ", ex);
        }
        return users;
    }

    @Override
    public User selectById(Integer id) throws CRMProjectRepositoryException {
        User user = null;
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_USER_BY_ID_QUERY + id)) {
            if (resultSet.next()) {
                user = getUser(resultSet);
                if (resultSet.next()) {
                    throw new SQLIntegrityConstraintViolationException("Count users more one");
                }
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT USER BY ID: ", ex);
        }
        return user;
    }

    @Override
    public List<User> selectAllUsersByTaskId(Integer taskId) throws CRMProjectRepositoryException {
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();

             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_ALL_USERS_FOR_TASK + taskId)) {
            while (resultSet.next()) {
                User user = selectById(resultSet.getInt(CROSS_TABLE_ID_USER));
                users.add(user);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS FOR TASK: ", ex);
        }
        return users;
    }

    @Override
    public List<User> selectAllUsersByRoleId(Integer roleId) throws CRMProjectRepositoryException {
        List<User> users = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_ALL_USERS_BY_ROLE + roleId)) {
            while (resultSet.next()) {
                User user = getUser(resultSet);
                users.add(user);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS FOR USER: ", ex);
        }
        return users;
    }

    @Override
    public User add(User user) throws CRMProjectRepositoryException {
        List<User> users = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            users.add(user);
            insert(users, preparedStatement);
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO USER - " + user + ": ", ex);
        }
        return users.get(0);
    }

    @Override
    public User update(User user) throws CRMProjectRepositoryException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_USER_QUERY)) {
            extracted(0, user, preparedStatement);

            preparedStatement.setInt(6, user.getId());

            if (preparedStatement.executeUpdate() > 0) {
                user.setId(user.getId());
            } else throw new CRMProjectRepositoryException("ERROR: UPDATE_USER - " + user + ": NO FOUND DATA BASE");
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE_USER - " + user + ": ", ex);
        }
        return user;
    }

    @Override
    public void remove(Integer userId) throws CRMProjectRepositoryException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                removeAllTasksByUser(userId);
                try (PreparedStatement preparedStatement = conn.prepareStatement(DELETE_USER_QUERY)) {
                    preparedStatement.setInt(1, userId);
                    if (preparedStatement.executeUpdate() != 1) {
                        throw new CRMProjectRepositoryException("ERROR: REMOVE_USER_BY_ID_ - " + userId + ": ");
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw new SQLException("TRANSACTION ROLLBACK: " + ex);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: REMOVE_USER - " + userId + ": ", ex);
        }
    }

    private void removeAllTasksByUser(Integer userId) throws CRMProjectRepositoryException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(DELETE_USER_ALL_TASKS_QUERY)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: DELETE_ALL_TASKS_BY_USER_BY_ID - " + userId + ": ", ex);
        }
    }

    private User getUser(ResultSet resultSet) throws SQLException, CRMProjectRepositoryException {

        User user = new User();
        user.setId(resultSet.getInt(ID_USER_COLUMN));
        user.setLogin(resultSet.getString(LOGIN_USER_COLUMN));
        user.setPsw(resultSet.getString(PSW_USER_COLUMN));
        Role role = Role.builder().id(resultSet.getInt(ROLE_USER_COLUMN)).roleName(resultSet.getString(ROLE_NAME_TABLE_ROLE)).build();
        user.setRole(role);
        user.setFirstName(resultSet.getString(FIRST_NAME_USER_COLUMN));
        user.setLastName(resultSet.getString(LAST_NAME_USER_COLUMN));

        return user;
    }

    private void insert(List<User> users, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < users.size(); i++) {
            extracted(i, users.get(i), preparedStatement);         //
        }
        int effectiveRows = preparedStatement.executeUpdate();
        if (effectiveRows == users.size()) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                for (int i = 0; i < effectiveRows; i++) {
                    if (generatedKeys.next()) {
                        users.get(i).setId(generatedKeys.getInt(ID_USER_COLUMN));
                    }
                }
            }
        }
    }

    /**
     * @param counter           - counter, determines which element is added counting from 0
     * @param user              - user
     * @param preparedStatement
     * @throws SQLException
     */
    private void extracted(int counter, User user, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1 + 5 * counter, user.getLogin());
        preparedStatement.setString(2 + 5 * counter, user.getPsw());
        preparedStatement.setInt(3 + 5 * counter, user.getRole().getId());
        preparedStatement.setString(4 + 5 * counter, user.getFirstName());
        preparedStatement.setString(5 + 5 * counter, user.getLastName());
    }

}
