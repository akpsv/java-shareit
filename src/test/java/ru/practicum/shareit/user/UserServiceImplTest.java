package ru.practicum.shareit.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository stubUserRepository;

    @Test
    void addUser_UserDto_ReturnsUserDtoWithId() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@mail.ru");
        UserDto userDto1 = TestHelper.createUserDto(-1L, "user1", "user1@mail.ru");
        UserDto expectedUserDto = TestHelper.createUserDto(1L, "user1", "user1@mail.ru");

        Mockito.when(stubUserRepository.save(Mockito.any(User.class))).thenReturn(user1);
        UserService userService = new UserServiceImpl(stubUserRepository);

        //Действия
        UserDto actualUserDto = userService.addUser(userDto1).get();

        //Проверка
        assertThat(actualUserDto, Matchers.samePropertyValuesAs(expectedUserDto));
    }

    @Test
    void getUsers_WhenCalled_ReturnsAllUsers() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@mail.ru");
        User user2 = TestHelper.createUser(2L, "user2", "user2@mail.ru");
        List<User> groupOfUsers = List.of(user1, user2);

        Mockito.when(stubUserRepository.findAll()).thenReturn(groupOfUsers);
        UserService userService = new UserServiceImpl(stubUserRepository);

        List<User> expectedGroupOfUsers = List.of(user1, user2);
        //Действия
        List<User> actualGroupOfUsers = userService.getUsers().get();
        //Проверка
        assertThat(actualGroupOfUsers.size(), Matchers.equalTo(expectedGroupOfUsers.size()));
    }

    @Test
    void updateUser_UserDtoAndUserId_ReturnsUpdatedUserDto() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@mail.ru");
        User updatedUser1 = TestHelper.createUser(1L, "user1update", "user1@mail.ru");
        UserDto updatingUserDto = TestHelper.createUserDto(1L, "user1update", "user1@mail.ru");

        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));
        Mockito.when(stubUserRepository.save(Mockito.any(User.class))).thenReturn(updatedUser1);
        UserService userService = new UserServiceImpl(stubUserRepository);

        UserDto expectedUserDto = TestHelper.createUserDto(1L, "user1update", "user1@mail.ru");

        //Действия
        UserDto actualUserDto = userService.updateUser(updatingUserDto, 1L).get();
        //Проверка
        assertThat(actualUserDto, Matchers.samePropertyValuesAs(expectedUserDto));
    }

    @Test
    void updateUser_EmailAlreadyExist_ThrowsRuntimeException() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@mail.ru");
        UserDto updatingUserDto = TestHelper.createUserDto(0L, null, "user1@mail.ru");

        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));
        UserService userService = new UserServiceImpl(stubUserRepository);

        String expectedMessage = "email уже существует";

        //Действия
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.updateUser(updatingUserDto, 1L).get()
        );
        String actualMessage = exception.getMessage();

        //Проверка
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getUserById_UserId_ReturnsUserDto() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@mail.ru");
        UserDto expectedUserDto = TestHelper.createUserDto(1L, "user1", "user1@mail.ru");

        Mockito.when(stubUserRepository.getReferenceById(Mockito.anyLong())).thenReturn(user1);

        UserService userService = new UserServiceImpl(stubUserRepository);

        //Действия
        UserDto actualUserDto = userService.getUserById(1L).get();
        //Проверка
        assertThat(actualUserDto, Matchers.samePropertyValuesAs(expectedUserDto));
    }

    @Test
    void deleteUserById_UserId_CallsDeleteByIdFromUserRepository() {
        //Подготовка
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(mockUserRepository);
        //Действия
        userService.deleteUserById(1L);
        //Проверка
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }
}
