package com.dansam0.steamdemo.dao;

import com.dansam0.steamdemo.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl implements RoleDao{

    private EntityManager entityManager;

    public RoleDaoImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public Role findRoleByName(String name) {

        TypedQuery<Role> theQuery = entityManager.createQuery("from Role where name=:roleName", Role.class);
        theQuery.setParameter("roleName", name);

        Role theRole = null;

        try {
            theRole = theQuery.getSingleResult();
        } catch (Exception e) {
            theRole = null;
        }

        return theRole;
    }

}
