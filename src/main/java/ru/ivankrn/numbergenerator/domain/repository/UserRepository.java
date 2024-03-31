package ru.ivankrn.numbergenerator.domain.repository;

import ru.ivankrn.numbergenerator.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    void save(User user);
    Optional<User> findByUsername(String username);
    long getCount();
    void delete(User user);

}
