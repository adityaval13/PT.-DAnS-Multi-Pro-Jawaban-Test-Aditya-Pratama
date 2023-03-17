package com.example.dans.models.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dans.models.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsernameAndPassword(String username, String password);
}
