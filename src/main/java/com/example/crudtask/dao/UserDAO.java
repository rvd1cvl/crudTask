package com.example.crudtask.dao;

import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    @Query("SELECT p FROM PhoneData p WHERE p.user.id = :userId")
    List<PhoneData> findUserPhones(Long userId);

    // Предполагается, что вы также создали класс EmailData с нужными аннотациями и связями
    @Query("SELECT e FROM EmailData e WHERE e.user.id = :userId")
    List<EmailData> findUserEmails(Long userId);

//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public User findById(Long userId) {
//        return entityManager.find(User.class, userId);
//    }
//
//    public User save(User user) {
//        return entityManager.merge(user);
//    }
//
//    public void delete(User user) {
//        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
//    }
//
//    public List<PhoneData> findUserPhones(Long userId) {
//        TypedQuery<PhoneData> query = entityManager.createQuery(
//                "SELECT p FROM PhoneData p WHERE p.user.id = :userId", PhoneData.class);
//        query.setParameter("userId", userId);
//        return query.getResultList();
//    }
//
//    public List<EmailData> findUserEmails(Long userId) {
//        TypedQuery<EmailData> query = entityManager.createQuery(
//                "SELECT e FROM EmailData e WHERE e.user.id = :userId", EmailData.class);
//        query.setParameter("userId", userId);
//        return query.getResultList();
//    }
//
//    public List<User> findAll() {
//        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
//        return query.getResultList();
//    }
//
//    public long count() {
//        return entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
//    }
}
