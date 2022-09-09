package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private int countOfId = 1;
    private Map<Integer, User> users = new HashMap<>();
    private Set<String> unicEmailsGroup = new HashSet<>();

    private int generateId() {
        return countOfId++;
    }

    @Override
    public Optional<User> add(User user) {
        return Optional.of(user).filter(addingUser -> !unicEmailsGroup.contains(addingUser.getEmail())).map(addingUser -> {
            User userWithId = addingUser.toBuilder().id(generateId()).build();
            users.put(userWithId.getId(), userWithId);
            unicEmailsGroup.add(userWithId.getEmail());
            return userWithId;
        });
    }

    @Override
    public <T, R> R update(Function<T, R> updateFunc, T t) {
        return updateFunc.apply(t);
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public Set<String> getUnicEmails() {
        return unicEmailsGroup;
    }

    public <T, R> R get(Function<T, R> getFunc, T t) {
        return getFunc.apply(t);
    }

    @Override
    public Optional<User> deleteById(int id) {
        return Optional.ofNullable(users.remove(id));
    }
}
