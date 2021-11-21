package com.itrex.java.lab.projectcrmspringboot.repository.impl.hibernate;

import com.itrex.java.lab.projectcrmspringboot.entity.Role;
import com.itrex.java.lab.projectcrmspringboot.exceptions.CRMProjectRepositoryException;
import com.itrex.java.lab.projectcrmspringboot.repository.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class HibernateRoleRepositoryImpl implements RoleRepository {

    private static final String ROLE_NAME = "roleName";
    private static final String ID_ROLE = "idRole";
    private static final String SELECT_ALL = "from Role r";
    private static final String UPDATE = "update Role set roleName = :roleName where id = :idRole";

    private final SessionFactory sessionFactory;

    @Autowired
    public HibernateRoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Role> selectAll() throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(SELECT_ALL, Role.class).list();
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ALL ROLES: ", e);
        }
    }

    @Override
    public Role selectById(Integer id) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {

            return session.find(Role.class, id);
        } catch (Exception e) {
            throw new CRMProjectRepositoryException("ERROR: SELECT ROLE BY ID: ", e);
        }
    }

    @Override
    public Role add(Role role) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            session.save(role);
            return role;
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: INSERT INTO ROLE - " + role.getRoleName() + ": ", ex);
        }
    }

    @Override
    public Role update(Role role) throws CRMProjectRepositoryException {
        try (Session session = sessionFactory.openSession()) {

            session.getTransaction().begin();
            Query query = session.createQuery(UPDATE);
            query.setParameter(ROLE_NAME, role.getRoleName());
            query.setParameter(ID_ROLE, role.getId());
            query.executeUpdate();
            session.getTransaction().commit();

            return Optional.ofNullable(session.get(Role.class, role.getId())).orElseThrow(() -> new CRMProjectRepositoryException("ROLE NO FOUND DATA BASE"));
        } catch (Exception ex) {
            throw new CRMProjectRepositoryException("ERROR: UPDATE_ROLE - " + role, ex);
        }
    }

}
