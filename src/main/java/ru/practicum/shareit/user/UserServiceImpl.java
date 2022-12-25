package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

//    @Autowired
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Transactional
    @Override
    public Optional<UserDto> addUser(UserDto userDto) {
        return UserMapper.toUser(userDto)
                .map(user -> userRepository.save(user))
                .flatMap(user -> UserMapper.toDto(user));
    }

    @Override
    public Optional<List<User>> getUsers() {
        return Optional.of(userRepository.findAll());
    }

    @Transactional
    @Override
    public Optional<UserDto> updateUser(final UserDto userDto, final long userId) {
        return userRepository.findById(userId)
                .map(user -> updateUserFromDto(userDto, user))
                .map(user -> userRepository.save(user))
                .flatMap(user -> UserMapper.toDto(user));
    }

    private User updateUserFromDto(UserDto dto, User user) {
        User updatedUser = user;
        if (dto.getName() == null && user.getEmail().equals(dto.getEmail())) {
            throw new RuntimeException("Такой email уже существует.");
        }
        if (dto.getName() != null) {
            updatedUser = updatedUser.toBuilder().name(dto.getName()).build();
        }
        if (dto.getEmail() != null) {
            updatedUser = updatedUser.toBuilder().email(dto.getEmail()).build();
        }
        return updatedUser;
    }

    @Override
    public Optional<UserDto> getUserById(long userId) {
        return Optional.of(userRepository.getReferenceById(userId))
                .flatMap(user -> UserMapper.toDto(user));
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
}
