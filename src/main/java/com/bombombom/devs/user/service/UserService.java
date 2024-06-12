package com.bombombom.devs.user.service;

import com.bombombom.devs.user.exception.ExistUsernameException;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.bombombom.devs.user.service.dto.SignupCommand;
import com.bombombom.devs.user.service.dto.UserProfileResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addUser(SignupCommand signupCommand) {
        if (userRepository.existsByUsername(signupCommand.username())) {
            throw new ExistUsernameException();
        }
        User user = User.builder()
                .username(signupCommand.username())
                .password(passwordEncoder.encode(signupCommand.password()))
                .introduce(signupCommand.introduce())
                .build();
        userRepository.save(user);
    }

    public UserProfileResult findByUsername(String username) {
        User user =  userRepository.findUserByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
        return UserProfileResult.fromEntity(user);
    }
}
