package com.example.uploadservice.authentication;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.uploadservice.authentication.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);
}
