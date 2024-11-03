package com.example.crudtask.dao;

import com.example.crudtask.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Account findByUserId(Long userId) {
        TypedQuery<Account> query = entityManager.createQuery(
                "SELECT a FROM Account a WHERE a.user.id = :userId", Account.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }

    public Account save(Account account) {
        if (account.getId() == null) {
            entityManager.persist(account);
            return account;
        } else {
            return entityManager.merge(account);
        }
    }

    public void delete(Account account) {
        entityManager.remove(entityManager.contains(account) ? account : entityManager.merge(account));
    }
}