package com.itrex.java.lab.projectcrmspringboot.repository.impl.hibernate;

import com.itrex.java.lab.projectcrmspringboot.entity.User;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.projectcrmspringboot.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Primary
@Repository
public class HibernateUserRepositoryImpl implements UserRepository {

    private static final String TASK_ID = "taskId";
    private static final String ROLE_ID = "roleId";
    private static final String USER_ID = "userId";
    private static final String SELECT_ALL = "select u from User u";
    private static final String SELECT_USER_BY_ID_With_TASKS = "select u from User u left join fetch u.tasks t where u.id = :userId";
    private static final String SELECT_ALL_USERS_BY_TASK = "select u from Task t join t.users u where t.id =:taskId";
    private static final String SELECT_ALL_USERS_BY_ROLE = "select u from Role r join r.users u where r.id =:roleId";

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<User> selectAll() throws CRMProjectRepositoryException {

        try {
            Session session = sessionFactory.openSession();
            return session.createQuery(SELECT_ALL, User.class).list();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS: ", ex);
        }
    }

    @Override
    public User selectById(Integer id) throws CRMProjectRepositoryException {

        try (Session session = sessionFactory.openSession()) {

            return session.get(User.class, id);
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT USER BY ID: " + ex);
        }
    }

    @Override
    public User selectByIdWithAllUserTasks(Integer id) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(SELECT_USER_BY_ID_With_TASKS, User.class)
                    .setParameter(USER_ID,id).getSingleResult();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT USER BY ID: " + ex);
        }
    }

    @Override
    public List<User> selectAllUsersByTaskId(Integer taskId) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(SELECT_ALL_USERS_BY_TASK, User.class)
                    .setParameter(TASK_ID, taskId)
                    .getResultList();
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS FOR TASK BY ID" + taskId + ": ", e);
        }
    }

    @Override
    public List<User> selectAllUsersByRoleId(Integer roleId) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(SELECT_ALL_USERS_BY_ROLE, User.class)
                    .setParameter(ROLE_ID, roleId)
                    .getResultList();
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS FOR ROLE BY ID" + roleId + ": ", e);
        }
    }

    @Override
    public User add(User user) throws CRMProjectRepositoryException {

        try (Session session = sessionFactory.openSession()) {
            session.save(user);
            return user;
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO USER - " + user + ": ", ex);
        }
    }

    @Override
    public User update(User user) throws CRMProjectRepositoryException {

        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            session.update(user);
            session.getTransaction().commit();
            return session.get(User.class, user.getId());
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE TASK " + user, e);
        }
    }

    @Override
    public void remove(Integer userId) throws CRMProjectRepositoryException {

        try (Session session = sessionFactory.openSession()) {
            session.getTransaction().begin();
            User user = session.get(User.class, userId);
            session.remove(user);
            session.getTransaction().commit();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: REMOVE_USER - " + userId + ": ", ex);
        }
    }

}
