package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@SpringBootTest
class UserServiceImplIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    void addUser_UserDto_ReturnsAddedUserDto() {
        //Подготовка
        UserDto userDto1 = TestHelper.createUserDto(0L, "User1", "user1@mail.ru");
        UserDto expectedUserDto = TestHelper.createUserDto(1L, "User1", "user1@mail.ru");

        //Действия
        UserDto actualUserDto = userService.addUser(userDto1).get();
        //Проверка
        assertThat(actualUserDto, samePropertyValuesAs(expectedUserDto, "id"));
    }

    @Test
    void getUsers_WhenCalled_ReturnsCorrectQuantityOfUsers() {
        //Подготовка
        userRepository.deleteAll();
        User user1 = TestHelper.createUser(1L, "User1", "user1@email.ru");
        User user2 = TestHelper.createUser(2L, "User2", "user2@email.ru");
        int expectedQuantityOfUsers = 2;

        userRepository.save(user1);
        userRepository.save(user2);

        //Действия
        List<User> actualGroupOfUsers = userService.getUsers().get();
        //Проверка
        assertThat(actualGroupOfUsers.size(), equalTo(expectedQuantityOfUsers));
    }

    @Test
    void updateUser_UserDtoWithNewData_ReturnsChangedOldUser() {
        //Подготовка
        userRepository.deleteAll();

        User userOld = TestHelper.createUser(0L, "User1", "user1@email.ru");
        User savedUser = userRepository.save(userOld);
        long userId = savedUser.getId();

        UserDto userDtoWithNewData =  TestHelper.createUserDto(userId, "User1new", "user1new@email.ru");
        UserDto expectedUserDto = userDtoWithNewData;
        //Действия

        UserDto actualUserDto = userService.updateUser(userDtoWithNewData, userId).get();
        //Проверка
        assertThat(actualUserDto, samePropertyValuesAs(expectedUserDto));
    }

    @Test
    void getUserById_UserId_ReturnsCorrectUserDto() {
        //Подготовка
        userRepository.deleteAll();
        User newUser1 = TestHelper.createUser(0L, "user1", "user1@email.ru");
        User newUser2 = TestHelper.createUser(0L, "user2", "user2@email.ru");
        User savedUser1 = userRepository.save(newUser1);
        User savedUser2 = userRepository.save(newUser2);
        long userId = savedUser2.getId();

        UserDto expectedUserDto = TestHelper.createUserDto(savedUser2.getId(), savedUser2.getName(), savedUser2.getEmail());
        //Действия
        UserDto actualUserDto = userService.getUserById(userId).get();
        //Проверка
        assertThat(actualUserDto, samePropertyValuesAs(expectedUserDto));
    }

    @Test
    void deleteUserById_DeletingSingleUser_ReturnsNumberOfRemainigUsers() {
        //Подготовка
        userRepository.deleteAll();
        User user1 = TestHelper.createUser(0L, "user1", "user1@email.ru");
        User user2 = TestHelper.createUser(0L, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> groupUfExistUsers = userRepository.findAll();
        long deletingUserId = groupUfExistUsers.get(0).getId();

        //Действия

        userService.deleteUserById(deletingUserId);
        List<User> actualGroupUfUsers = userRepository.findAll();

        //Проверка
        assertThat(actualGroupUfUsers.size(), equalTo(1));

    }
}