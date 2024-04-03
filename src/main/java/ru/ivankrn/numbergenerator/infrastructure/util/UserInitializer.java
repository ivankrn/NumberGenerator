package ru.ivankrn.numbergenerator.infrastructure.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ivankrn.numbergenerator.infrastructure.security.Role;
import ru.ivankrn.numbergenerator.infrastructure.security.User;
import ru.ivankrn.numbergenerator.infrastructure.security.UserRepository;

@Component
public class UserInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserInitializer.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
