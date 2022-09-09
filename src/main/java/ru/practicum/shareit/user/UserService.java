package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> addUser(UserDto userDto);

    Optional<List<User>> getUsers();

    Optional<UserDto> updateUser(UserDto userDto, int userId);

    Optional<User> getUserById(int userId);

    Optional<Boolean> deleteUserById(int userId);
}
