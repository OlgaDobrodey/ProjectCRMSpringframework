package com.itrex.java.lab.projectcrmspringboot.repository.impl.hibernate;

import com.itrex.java.lab.projectcrmspringboot.entity.Task;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.projectcrmspringboot.repository.TaskRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
public class HibernateTaskRepositoryImpl implements TaskRepository {

    private static final String USER_ID = "userId";
    private static final String TASK_ID = "taskId";

    private static final String SELECT_ALL = "select t from Task t";
    private static final String SELECT_TASK_BY_ID_With_USERS = "select t from Task t left join fetch t.users u where t.id = :taskId";
    private static final String SELECT_ALL_TASKS_BY_USER = "select t from User u join u.tasks t where u.id =:userId";

    private final SessionFactory sessionFactory;

    @Autowired
    public HibernateTaskRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Task> selectAll() throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(SELECT_ALL, Task.class).list();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL TASK: " + ex);
        }
    }

    @Override
    public Task selectById(Integer id) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Task.class, id);
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT TASK BY ID: " + ex);
        }
    }

    @Override
    public Task selectByIdWithAllTaskUsers(Integer id) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(SELECT_TASK_BY_ID_With_USERS, Task.class)
                    .setParameter(TASK_ID,id).getSingleResult();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT USER BY ID: " + ex);
        }
    }

    @Override
    public List<Task> selectAllTasksByUserId(Integer id) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(SELECT_ALL_TASKS_BY_USER, Task.class)
                    .setParameter(USER_ID, id)
                    .getResultList();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL TASK FOR USER: ", ex);
        }
    }

    @Override
    public Task add(Task task) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            session.save(task);
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO TASK- " + task + ": ", ex);
        }
        return task;
    }

    @Override
    public Task update(Task task) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.update(task);
            session.getTransaction().commit();
            return session.get(Task.class, task.getId());
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE TASK " + task, e);
        }
    }

    @Override
    public void remove(Integer taskId) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            Task task = session.get(Task.class, taskId);
            session.delete(task);
            session.getTransaction().commit();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: REMOVE_TASK - " + taskId + ": ", ex);
        }
    }

}
