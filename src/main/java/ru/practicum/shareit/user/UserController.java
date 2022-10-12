package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Optional<UserDto> addUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return Optional.ofNullable(userDto).flatMap(dto -> userService.addUser(dto));
    }

    @PatchMapping("/{userId}")
    public Optional<UserDto> updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        return Optional.ofNullable(userDto).flatMap(dto -> userService.updateUser(dto, userId));
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserById(@PathVariable int userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public Optional<List<User>> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/{userId}")
    public Optional<Boolean> deleteUserById(@PathVariable int userId) {
        return userService.deleteUserById(userId);
    }
}
