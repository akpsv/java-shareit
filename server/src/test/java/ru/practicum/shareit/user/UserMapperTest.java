package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

class UserMapperTest {

    @Test
    void toDto_User_ReturnsUserDto() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "User1", "user1@mail.ru");
        UserDto expectedUserDto = TestHelper.createUserDto(1L, "User1", "user1@mail.ru");

        //Действия
        UserDto actualUserDto = UserMapper.toDto(user1).get();

        //Проверка
        assertThat(actualUserDto, samePropertyValuesAs(expectedUserDto, "id"));
    }

    @Test
    void toUser_UserDto_ReturnsUser() {
        //Подготовка
        UserDto userDto1 = TestHelper.createUserDto(1L, "User1", "user1@mail.ru");
        User expectedUser = TestHelper.createUser(1L, "User1", "user1@mail.ru");
        //Действия
        User actualUser = UserMapper.toUser(userDto1).get();
        //Проверка
        assertThat(actualUser, samePropertyValuesAs(expectedUser, "id"));
    }


}