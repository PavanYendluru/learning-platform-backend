package com.learningplatform.backend.config;

import com.learningplatform.backend.entity.User;
import com.learningplatform.backend.enums.Role;
import com.learningplatform.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Optional first-admin bootstrap, disabled unless explicitly configured. */
@Configuration
public class AdminUserInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Bean
    ApplicationRunner initializeAdminUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.bootstrap.enabled:false}") boolean enabled,
            @Value("${app.admin.bootstrap.name:}") String name,
            @Value("${app.admin.bootstrap.email:}") String email,
            @Value("${app.admin.bootstrap.password:}") String password) {

        return args -> {
            if (!enabled) {
                return;
            }
            if (name.isBlank() || email.isBlank() || password.length() < 8) {
                throw new IllegalStateException(
                        "Admin bootstrap requires a name, email, and password of at least 8 characters");
            }

            String normalizedEmail = email.trim().toLowerCase();
            if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                log.info("Admin bootstrap skipped because {} already exists", normalizedEmail);
                return;
            }

            User admin = new User();
            admin.setName(name.trim());
            admin.setEmail(normalizedEmail);
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            log.info("Initial admin account created for {}", normalizedEmail);
        };
    }
}
