package com.itrex.java.lab.repository.impl.hibernate;

import com.itrex.java.lab.entity.User;
import com.itrex.java.lab.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Repository
public class HibernateUserRepositoryImpl implements UserRepository {

    private static final String TASK_ID = "taskId";
    private static final String ROLE_ID = "roleId";
    private static final String SELECT_ALL = "from User u";
    private static final String SELECT_ALL_USERS_BY_TASK = "select u from Task t join t.users u where t.id =:taskId";
    private static final String SELECT_ALL_USERS_BY_ROLE = "select u from Role r join r.users u where r.id =:roleId";

    private final SessionFactory sessionFactory;

    @Autowired
    public HibernateUserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public List<User> selectAll() throws CRMProjectRepositoryException {

        try {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery(SELECT_ALL, User.class).list();
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS: ", ex);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public User selectById(Integer id) throws CRMProjectRepositoryException {

        try {
            Session session = sessionFactory.getCurrentSession();
            return session.get(User.class, id);
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: SELECT TASK BY ID: " + ex);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public List<User> selectAllUsersByTaskId(Integer taskId) throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();

            return session.createQuery(SELECT_ALL_USERS_BY_TASK, User.class)
                    .setParameter(TASK_ID, taskId)
                    .getResultList();
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS FOR TASK BY ID" + taskId + ": ", e);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public List<User> selectAllUsersByRoleId(Integer roleId) throws CRMProjectRepositoryException {
        try {
            Session session = sessionFactory.getCurrentSession();

            return session.createQuery(SELECT_ALL_USERS_BY_ROLE, User.class)
                    .setParameter(ROLE_ID, roleId)
                    .getResultList();
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL USERS FOR ROLE BY ID" + roleId + ": ", e);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public User add(User user) throws CRMProjectRepositoryException {

        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(user);
            return user;
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO USER - " + user + ": ", ex);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public User update(User user) throws CRMProjectRepositoryException {

        try {
            Session session = sessionFactory.getCurrentSession();
            session.update(user);
            return session.get(User.class, user.getId());
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE TASK " + user, e);
        }
    }

    @Override
    @Transactional(rollbackFor = {CRMProjectRepositoryException.class})
    public void remove(Integer userId) throws CRMProjectRepositoryException {
        try {

            Session session = sessionFactory.getCurrentSession();
            session.remove(session.get(User.class, userId));
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: REMOVE_USER - " + userId + ": ", ex);
        }
    }

}
