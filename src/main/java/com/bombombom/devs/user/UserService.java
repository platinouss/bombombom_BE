package com.bombombom.devs.user;

import com.bombombom.devs.user.controller.dto.SignupRequest;
import com.bombombom.devs.user.exception.ExistUsernameException;
import com.bombombom.devs.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void addUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.username())) {
            throw new ExistUsernameException();
        }
        User user = User.builder()
            .username(signupRequest.username())
            .password(bCryptPasswordEncoder.encode(signupRequest.password()))
            .introduce(signupRequest.introduce())
            .build();
        userRepository.save(user);
    }

    public void removeUser(String username) {
        userRepository.deleteByUsername(username);
    }
}
