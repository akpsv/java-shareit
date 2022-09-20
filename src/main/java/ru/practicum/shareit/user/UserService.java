package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserDto> addUser(UserDto userDto);

    Optional<List<User>> getUsers();

    Optional<UserDto> updateUser(UserDto userDto, long userId);

    Optional<UserDto> getUserById(long userId);

    void deleteUserById(long userId);
}
