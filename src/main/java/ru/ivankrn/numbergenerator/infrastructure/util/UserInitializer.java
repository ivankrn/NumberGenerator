package ru.ivankrn.numbergenerator.infrastructure.util;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.ivankrn.numbergenerator.domain.entity.Role;
import ru.ivankrn.numbergenerator.domain.entity.User;
import ru.ivankrn.numbergenerator.domain.repository.UserRepository;

@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserInitializer.class);

    public UserInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.getCount() != 0) {
            logger.info("Users database isn't empty, skipping default user initialization.");
            return;
        }
        User admin = new User(null, "admin", passwordEncoder.encode("admin"), Role.ADMIN);
        userRepository.save(admin);
        logger.info("Default admin user was added");
    }

}
