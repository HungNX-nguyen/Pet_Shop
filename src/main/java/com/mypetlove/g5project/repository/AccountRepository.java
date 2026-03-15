package com.mypetlove.g5project.repository;

import com.mypetlove.g5project.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmailOrUsername(String email, String username);

    @Query("SELECT a FROM Account a WHERE a.isActive = true")
    List<Account> findAllActiveAccounts();

    @Query("SELECT a FROM Account a WHERE a.fullName LIKE %:keyword% OR a.email LIKE %:keyword%")
    List<Account> searchAccounts(@Param("keyword") String keyword);
}