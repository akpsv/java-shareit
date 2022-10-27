package ru.practicum.shareit.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void save_UserWithoutId_UserWithId(){
        //Подготовка
        User user1 = TestHelper.createUser(0L, "User1", "user1@mail.ru");
        User expectedUser = TestHelper.createUser(1L, "User1", "user1@mail.ru");;

        //Действия
        User actualUser = userRepository.save(user1);

        //Проверка
        assertThat(actualUser, samePropertyValuesAs(expectedUser));
    }

}