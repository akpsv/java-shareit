package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Repository
public interface UserStorage {
    Optional<User> add(User user);

    <T, R> R update(Function<T, R> updateFunc, T t);

    Map<Integer, User> getUsers();

    Set<String> getUnicEmails();

    <T, R> R get(Function<T, R> getFunc, T t);

    Optional<User> deleteById(int id);
}
