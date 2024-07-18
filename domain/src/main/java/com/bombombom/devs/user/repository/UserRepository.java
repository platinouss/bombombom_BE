package com.bombombom.devs.user.repository;

import com.bombombom.devs.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}
