package com.bombombom.devs.user.service;

import com.bombombom.devs.user.exception.ExistUsernameException;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import com.bombombom.devs.user.service.dto.SignupCommand;
import com.bombombom.devs.user.service.dto.UserProfileResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addUser(SignupCommand signupCommand) {
        if (userRepository.existsByUsername(signupCommand.username())) {
            throw new ExistUsernameException();
        }
        User user = User.signup(signupCommand.username(),
            passwordEncoder.encode(signupCommand.password()), signupCommand.introduce());

        userRepository.save(user);
    }

    public UserProfileResult findById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User Not Found"));
        return UserProfileResult.fromEntity(user);
    }
}
