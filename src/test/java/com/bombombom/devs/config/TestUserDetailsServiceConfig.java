package com.bombombom.devs.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@TestConfiguration
public class TestUserDetailsServiceConfig {

    @Bean
    public UserDetailsService testUserDetailsService() {
        return new UserDetailsService() {
            private final List<UserDetails> users = new ArrayList<>();

            {
                // 사용자 목록 초기화
                users.add(User.withUsername("testuser")
                    .password("{noop}password1")
                    .roles("USER")
                    .build());

                users.add(User.withUsername("admin")
                    .password("{noop}password2")
                    .roles("ADMIN")
                    .build());
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return users.stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            }
        };
    }
}
