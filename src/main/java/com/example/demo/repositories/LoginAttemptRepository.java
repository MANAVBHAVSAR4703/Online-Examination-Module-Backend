package com.example.demo.repositories;

import com.example.demo.models.LoginAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempts,Long> {
}
