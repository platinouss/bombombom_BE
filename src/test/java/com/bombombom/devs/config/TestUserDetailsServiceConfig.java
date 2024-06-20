package com.bombombom.devs.config;

import com.bombombom.devs.global.security.AppUserDetails;
import com.bombombom.devs.user.models.Role;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@TestConfiguration
public class TestUserDetailsServiceConfig {

    @Bean
    public UserDetailsService testUserDetailsService() {
        return new UserDetailsService() {
            private final List<AppUserDetails> users = new ArrayList<>();

            {
                users.add(
                    AppUserDetails.builder()
                        .id(1L)
                        .username("testuser")
                        .password("password")
                        .authorities(List.of(new SimpleGrantedAuthority(Role.USER.getName())))
                        .build()
                );
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
