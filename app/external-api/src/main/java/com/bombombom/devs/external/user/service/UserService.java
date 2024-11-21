package com.bombombom.devs.external.user.service;

import com.bombombom.devs.core.exception.DuplicationException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.external.points.service.PointsService;
import com.bombombom.devs.external.user.service.dto.SignupCommand;
import com.bombombom.devs.external.user.service.dto.UserProfileResult;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PointsService pointsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addUser(SignupCommand signupCommand) {
        if (userRepository.existsByUsername(signupCommand.username())) {
            throw new DuplicationException(ErrorCode.DUPLICATED_USERNAME);
        }
        User user = signupCommand.toEntity(passwordEncoder);
        user.initPointHistory();
        userRepository.save(user);
    }

    public UserProfileResult findById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Long currentPoints = pointsService.getCurrentPoints(userId);
        return UserProfileResult.fromEntity(user, currentPoints);
    }
}
