package com.itv.internship.repository;

import com.itv.internship.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCustomerNameIgnoreCase(String customerName);
}