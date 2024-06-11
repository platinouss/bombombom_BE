package com.bombombom.devs.user;

import com.bombombom.devs.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    void deleteByUsername(String username);

    boolean existsByUsername(String username);
}
