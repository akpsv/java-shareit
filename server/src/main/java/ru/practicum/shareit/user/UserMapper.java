package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserMapper {

    public static Optional<UserDto> toDto(User user) {
        return Optional.ofNullable(user)
                .map(userFromModel -> UserDto.builder()
                        .id(userFromModel.getId())
                        .name(userFromModel.getName())
                        .email(userFromModel.getEmail())
                        .build()
                );
    }

    public static Optional<User> toUser(UserDto userDto) {
        return Optional.ofNullable(userDto).map(dto -> {
                    User user = User.builder().id(userDto.getId()).build();
                    if (dto.getName() != null) {
                        user = user.toBuilder().name(dto.getName()).build();
                    }
                    if (dto.getEmail() != null) {
                        user = user.toBuilder().email(dto.getEmail()).build();
                    }
                    return user;
                }
        );
    }
}
