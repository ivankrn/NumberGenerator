package ru.ivankrn.numbergenerator.infrastructure.security;

import java.util.Optional;

public interface UserRepository {

    void save(User user);
    Optional<User> findByUsername(String username);
    long getCount();
    void delete(User user);

}
