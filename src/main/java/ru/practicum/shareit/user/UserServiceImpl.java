package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Optional<UserDto> addUser(UserDto userDto) {
        return UserMapper.toUser(userDto)
                .map(user -> userStorage.add(user))
                .flatMap(user -> UserMapper.toDto(user.get()));
    }

    @Override
    public Optional<List<User>> getUsers() {
        Function<Map<Integer, User>, Optional<List<User>>> getUsersFunc = (entries ->
                Optional.of(entries.values().stream().collect(Collectors.toList()))
        );
        return userStorage.get(getUsersFunc, userStorage.getUsers());
    }

    @Override
    public Optional<UserDto> updateUser(final UserDto userDto, final int userId) {
        Function<UserDto, Optional<UserDto>> updateFunc = (dto ->
                getUserById(userId).map(user -> updateUserFromDto(dto, user))
                        .flatMap(newUser -> {
                            Optional.ofNullable(userStorage.getUsers().replace(newUser.getId(), newUser));
                            return UserMapper.toDto(newUser);
                        })
        );
        return userStorage.update(updateFunc, userDto);
    }

    private User updateUserFromDto(UserDto dto, User user) {
        User updatedUser = user;
        if (dto.getName() == null && userStorage.getUnicEmails().contains(dto.getEmail())) {
            throw new RuntimeException("Такой email уже существует.");
        }
        if (dto.getName() != null) {
            updatedUser = updatedUser.toBuilder().name(dto.getName()).build();
        }
        if (dto.getEmail() != null) {
            userStorage.getUnicEmails().removeIf(email -> user.getEmail().equals(email));
            updatedUser = updatedUser.toBuilder().email(dto.getEmail()).build();
            userStorage.getUnicEmails().add(dto.getEmail());
        }
        return updatedUser;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        Function<Integer, Optional<User>> getUserByIdFunc = ((id) ->
                Optional.ofNullable(userStorage.getUsers().get(id)));
        return userStorage.get(getUserByIdFunc, userId);
    }

    @Override
    public Optional<Boolean> deleteUserById(int userId) {
        return userStorage.deleteById(userId)
                .map(user -> userStorage.getUnicEmails().remove(user.getEmail()));
    }
}
