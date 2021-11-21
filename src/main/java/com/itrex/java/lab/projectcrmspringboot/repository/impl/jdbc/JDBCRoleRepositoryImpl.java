package com.itrex.java.lab.projectcrmspringboot.repository.impl.jdbc;

import com.itrex.java.lab.projectcrmspringboot.entity.Role;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.projectcrmspringboot.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Repository
@Qualifier("JDBCRoleRepository")
public class JDBCRoleRepositoryImpl implements RoleRepository {

    private static final String ID_ROLE_COLUMN = "id";
    private static final String NAME_ROLE_COLUMN = "role_name";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM role";
    private static final String SELECT_ROLE_BY_ID_QUERY = "SELECT * FROM role WHERE id = ";
    private static final String INSERT_ROLE_QUERY = "INSERT INTO role(role_name) VALUES (?)";
    private static final String UPDATE_ROLE_QUERY = "UPDATE role SET role_name = ? WHERE id = ?";

    private final DataSource dataSource;

    @Autowired
    public JDBCRoleRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Role> selectAll() throws CRMProjectRepositoryException {
        List<Role> roles = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_ALL_QUERY)) {
            while (resultSet.next()) {
                Role role = getRole(resultSet);
                roles.add(role);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL ROLES: ", ex);
        }
        return roles;
    }

    @Override
    public Role selectById(Integer id) throws CRMProjectRepositoryException {
        Role role = null;
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_ROLE_BY_ID_QUERY + id)) {
            if (resultSet.next()) {
                role = getRole(resultSet);
                if (resultSet.next()) {
                    throw new SQLIntegrityConstraintViolationException("ERROR: SELECT ROLE BY ID: Count roles more one");
                }
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ROLE BY ID: ", ex);
        }
        return role;
    }

    @Override
    public Role add(Role role) throws CRMProjectRepositoryException {
        List<Role> roles = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(INSERT_ROLE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            roles.add(role);
            insert(roles, preparedStatement);
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO ROLE - " + role.getRoleName() + ": ", ex);
        }
        return roles.get(0);
    }

    @Override
    public Role update(Role role) throws CRMProjectRepositoryException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_ROLE_QUERY)) {

            preparedStatement.setString(1, role.getRoleName());
            preparedStatement.setInt(2, role.getId());
            if (preparedStatement.executeUpdate() > 0) {
                role.setId(role.getId());
            } else throw new CRMProjectRepositoryException("ROLE NO FOUND DATA BASE");
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE_ROLE - " + role, ex);
        }
        return role;
    }

    private void insert(List<Role> roles, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 1; i <= roles.size(); i++) {
            preparedStatement.setString(i, roles.get(i - 1).getRoleName());
        }
        int effectiveRows = preparedStatement.executeUpdate();
        if (effectiveRows == roles.size()) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                for (int i = 0; i < effectiveRows; i++) {
                    if (generatedKeys.next()) {
                        roles.get(i).setId(generatedKeys.getInt(ID_ROLE_COLUMN));
                    }
                }
            }
        }
    }

    private Role getRole(ResultSet resultSet) throws SQLException {
        Role role = new Role();
        role.setId(resultSet.getInt(ID_ROLE_COLUMN));
        role.setRoleName(resultSet.getString(NAME_ROLE_COLUMN));
        return role;
    }

}
