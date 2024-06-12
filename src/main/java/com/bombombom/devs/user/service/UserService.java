package com.bombombom.devs.user.service;

import com.bombombom.devs.user.UserRepository;
import com.bombombom.devs.user.exception.ExistUsernameException;
import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.service.dto.SignupCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void addUser(SignupCommand signupCommand) {
        if (userRepository.existsByUsername(signupCommand.username())) {
            throw new ExistUsernameException();
        }
        User user = User.builder()
                .username(signupCommand.username())
                .password(bCryptPasswordEncoder.encode(signupCommand.password()))
                .introduce(signupCommand.introduce())
                .build();
        userRepository.save(user);
    }
}
