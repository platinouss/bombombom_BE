package com.bombombom.devs.common.config;

import com.bombombom.devs.common.CustomFlushEntityEventListener;
import com.bombombom.devs.security.AppUserDetails;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class JpaConfiguration implements AuditorAware<Long> {

    private final EntityManagerFactory entityManagerFactory;
    private final CustomFlushEntityEventListener customFlushEntityEventListener;

    @PostConstruct
    protected void init() {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(
            SessionFactoryImpl.class);

        EventListenerRegistry eventListenerRegistry = sessionFactory.getServiceRegistry()
            .getService(EventListenerRegistry.class);

        Assert.notNull(eventListenerRegistry, "EventListenerRegistry Is Null");

        eventListenerRegistry.getEventListenerGroup(EventType.FLUSH_ENTITY).clearListeners();
        eventListenerRegistry.getEventListenerGroup(EventType.FLUSH_ENTITY)
            .appendListener(customFlushEntityEventListener);
    }

    @NonNull
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof AppUserDetails userDetails) {

            return Optional.of(userDetails.getId());
        } else {
            return Optional.empty();
        }
    }
}