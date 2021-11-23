package com.itrex.java.lab.repository.impl.jdbc;

import com.itrex.java.lab.entity.Status;
import com.itrex.java.lab.entity.Task;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Repository
@Qualifier("JDBCTaskRepository")
public class JDBCTaskRepositoryImpl implements TaskRepository {

    private static final String ID_TASK_COLUMN = "id";
    private static final String TITLE_TASK_COLUMN = "title";
    private static final String STATUS_TASK_COLUMN = "status";
    private static final String DEADLINE_TASK_COLUMN = "deadline";
    private static final String INFO_TASK_COLUMN = "info";
    private static final String CROSS_TABLE_ID_TASK = "tasks_id";

    private static final String SELECT_ALL_QUERY = "SELECT * FROM crm.task";
    private static final String SELECT_TASK_BY_ID_QUERY = "SELECT * FROM crm.task WHERE id = ";
    private static final String SELECT_ALL_TASKS_FOR_USER = "SELECT tasks_id FROM crm.user_task WHERE users_id = ";
    private static final String INSERT_TASK_QUERY = "INSERT INTO crm.task(title, status, deadline, info) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_TASK_QUERY = "UPDATE crm.task SET title=?, status=?, deadline=?, info=?  WHERE id = ?";
    private static final String DELETE_TASK_QUERTY = "DELETE FROM crm.task WHERE id = ?";

    private DataSource dataSource;

    @Autowired
    public JDBCTaskRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Task> selectAll() throws CRMProjectRepositoryException {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_ALL_QUERY)) {

            while (resultSet.next()) {
                Task task = getTask(resultSet);
                tasks.add(task);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL TASK: " + ex);
        }
        return tasks;
    }

    @Override
    public Task selectById(Integer id) throws CRMProjectRepositoryException {
        Task task = null;
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_TASK_BY_ID_QUERY + id)) {
            if (resultSet.next()) {
                task = getTask(resultSet);
                if (resultSet.next()) {
                    throw new SQLIntegrityConstraintViolationException("Count tasks more one");
                }
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT TASK BY ID: " + ex);
        }
        return task;
    }

    @Override
    public List<Task> selectAllTasksByUserId(Integer id) throws CRMProjectRepositoryException {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement();
             ResultSet resultSet = stm.executeQuery(SELECT_ALL_TASKS_FOR_USER + id)) {
            while (resultSet.next()) {
                Task task = selectById(resultSet.getInt(CROSS_TABLE_ID_TASK));
                tasks.add(task);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL TASK FOR USER: ", ex);
        }
        return tasks;
    }

    @Override
    public Task add(Task task) throws CRMProjectRepositoryException {
        List<Task> tasks = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(INSERT_TASK_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            tasks.add(task);
            insert(tasks, preparedStatement);
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO TASK- " + task + ": ", ex);
        }
        return tasks.get(0);
    }

    @Override
    public Task update(Task task) throws CRMProjectRepositoryException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_TASK_QUERY)) {
            extracted(0, task, preparedStatement);

            preparedStatement.setInt(5, task.getId());
            if (preparedStatement.executeUpdate() > 0) {
                task.setId(task.getId());
            } else throw new CRMProjectRepositoryException("TASK NO FOUND DATA BASE");
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE TASK BY ID- " + task + ": ", ex);
        }
        return task;
    }

    @Override
    public void remove(Integer taskId) throws CRMProjectRepositoryException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement preparedStatement = conn.prepareStatement(DELETE_TASK_QUERTY)) {
                    preparedStatement.setInt(1, taskId);
                    if (preparedStatement.executeUpdate() != 1) {
                        throw new CRMProjectRepositoryException("ERROR: REMOVE_TASK - " + taskId);
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw new SQLException("TRANSACTION ROLLBACK: ", ex);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new CRMProjectRepositoryException("ERROR: REMOVE_TASK - " + taskId + ": ", ex);
        }
    }

    private Task getTask(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getInt(ID_TASK_COLUMN));
        task.setTitle(resultSet.getString(TITLE_TASK_COLUMN));
        task.setStatus(Status.valueOf(resultSet.getString(STATUS_TASK_COLUMN)));
        Date date = resultSet.getDate(DEADLINE_TASK_COLUMN);
        task.setDeadline(date != null ? date.toLocalDate() : null);
        task.setInfo(resultSet.getString(INFO_TASK_COLUMN));

        return task;
    }

    private void insert(List<Task> tasks, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < tasks.size(); i++) {
            extracted(i, tasks.get(i), preparedStatement);         //
        }
        int effectiveRows = preparedStatement.executeUpdate();
        if (effectiveRows == tasks.size()) {
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                for (int i = 0; i < effectiveRows; i++) {
                    if (generatedKeys.next()) {
                        tasks.get(i).setId(generatedKeys.getInt(ID_TASK_COLUMN));
                    }
                }
            }
        }
    }

    /**
     * @param counter           - counter, determines which element is added counting from 0
     * @param task              - task
     * @param preparedStatement
     * @throws SQLException
     */
    private void extracted(int counter, Task task, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1 + 4 * counter, task.getTitle());
        preparedStatement.setString(2 + 4 * counter, task.getStatus().name());
        preparedStatement.setDate(3 + 4 * counter, task.getDeadline() != null ? Date.valueOf(task.getDeadline()) : null);
        preparedStatement.setString(4 + 4 * counter, task.getInfo());
    }

}
