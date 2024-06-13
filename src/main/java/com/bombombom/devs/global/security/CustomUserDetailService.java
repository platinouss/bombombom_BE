package com.bombombom.devs.global.security;

import com.bombombom.devs.user.models.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), user.getPassword(), Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getName())));
    }

}
