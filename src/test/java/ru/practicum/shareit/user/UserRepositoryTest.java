package ru.practicum.shareit.user;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void save_UserWithoutId_ReturnsUserWithId() {
        //Подготовка
        User user1 = TestHelper.createUser(0L, "User1", "user1@mail.ru");
        //Действия
        User actualUser = userRepository.save(user1);
        User userById = userRepository.findById(actualUser.getId()).get();
        //Проверка
        assertThat(userById.getId(), Matchers.not(0L));
    }

    @Test
    void findById_UserId_ReturnsCorrectUser() {
        //Подготовка
        userRepository.deleteAll();
        userRepository.flush();

        User user1 = TestHelper.createUser(0L, "User1", "user1@mail.ru");
        User expectedUser = userRepository.save(user1);

        //Действия
        User actualUserById = userRepository.findById(expectedUser.getId()).get();
        //Проверка
        assertThat(actualUserById, Matchers.samePropertyValuesAs(actualUserById));
    }

    @Test
    void findAll_WhenCalled_ReturnsAllUsers() {
        //Подготовка
        userRepository.deleteAll();
        userRepository.flush();

        User user1 = TestHelper.createUser(0L, "User1", "user1@mail.ru");
        User user2 = TestHelper.createUser(0L, "User2", "user2@mail.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        int expectedQuantityOfUsers = 2;

        //Действия
        int actualQuantityOfUsers = userRepository.findAll().size();
        //Проверка
        assertThat(actualQuantityOfUsers, Matchers.equalTo(expectedQuantityOfUsers));
    }
}
