package com.example.crudtask.dao;

import com.example.crudtask.entity.EmailData;
import com.example.crudtask.entity.PhoneData;
import com.example.crudtask.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
    @Query("SELECT p FROM PhoneData p WHERE p.user.id = :userId")
    List<PhoneData> findUserPhones(Long userId);

    @Query("SELECT e FROM EmailData e WHERE e.user.id = :userId")
    List<EmailData> findUserEmails(Long userId);
    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.phones p " +
            "LEFT JOIN u.emails e " +
            "WHERE (:dateOfBirth IS NULL OR u.dateOfBirth > :dateOfBirth) " +
            "AND (:phone IS NULL OR p.phone = :phone) " +
            "AND (:name IS NULL OR u.name LIKE CONCAT(:name, '%')) " +
            "AND (:email IS NULL OR e.email = :email)")
    Page<User> findUsersByFilters(
            @Param("dateOfBirth") Optional<LocalDate> dateOfBirth,
            @Param("phone") Optional<String> phone,
            @Param("name") Optional<String> name,
            @Param("email") Optional<String> email,
            Pageable pageable
    );

    @Query("SELECT u FROM User u JOIN u.emails e WHERE e.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN u.phones p WHERE p.phone = :phone")
    User findByPhone(@Param("phone") String phone);
}
