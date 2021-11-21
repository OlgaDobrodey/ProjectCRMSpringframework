package com.itrex.java.lab.projectcrmspringboot.repository.impl.hibernate;

import com.itrex.java.lab.projectcrmspringboot.entity.Task;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.projectcrmspringboot.repository.TaskRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Repository
public class HibernateTaskRepositoryImpl implements TaskRepository {

    private static final String USER_ID = "userId";

    private static final String SELECT_ALL = "from Task t";
    private static final String SELECT_ALL_TASKS_BY_USER = "select t from User u join u.tasks t where u.id =:userId";

    private final SessionFactory sessionFactory;

    @Autowired
    public HibernateTaskRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public List<Task> selectAll() throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery(SELECT_ALL, Task.class).list();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL TASK: " + ex);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public Task selectById(Integer id) throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();
            return session.get(Task.class, id);
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT TASK BY ID: " + ex);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public List<Task> selectAllTasksByUserId(Integer id) throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();

            return session.createQuery(SELECT_ALL_TASKS_BY_USER, Task.class)
                    .setParameter(USER_ID, id)
                    .getResultList();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL TASK FOR USER: ", ex);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public Task add(Task task) throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(task);
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO TASK- " + task + ": ", ex);
        }
        return task;
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public Task update(Task task) throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.update(task);
            return session.get(Task.class, task.getId());
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE TASK " + task, e);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public void remove(Integer taskId) throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();

            Task task = session.get(Task.class, taskId);
            session.delete(task);
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: REMOVE_TASK - " + taskId + ": ", ex);
        }
    }

}
