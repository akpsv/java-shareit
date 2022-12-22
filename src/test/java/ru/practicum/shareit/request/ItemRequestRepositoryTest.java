package ru.practicum.shareit.request;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional
    void getItemRequestsCreatedAnotherUsers_RequestorId_ReturnsAllRequestsExceptPassedRequestorId() {
        //Подготовка
        itemRequestRepository.deleteAll();
        itemRequestRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        User user2 = TestHelper.createUser(2L, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest itemRequest2 = TestHelper.createItemRequest(2L, 2L, new HashSet<>());
        ItemRequest save1 = itemRequestRepository.save(itemRequest1);
        ItemRequest save2 = itemRequestRepository.save(itemRequest2);
        //Действия
        Optional<List<ItemRequest>> actualItemRequestsExceptRequestor = itemRequestRepository
                .getItemRequestsCreatedAnotherUsers(1L, entityManager, 0, 10);

        //Проверка
        assertThat(actualItemRequestsExceptRequestor.get().size(), Matchers.equalTo(1));
        assertThat(actualItemRequestsExceptRequestor.get().get(0).getRequestor(), Matchers.equalTo(2L));
    }

    @Test
    @Transactional
    void getItemRequestsCreatedAnotherUsers_NumberOfElementsIsNull_ReturnsAllRequestsWithoutPagination() {
        //Подготовка
        itemRequestRepository.deleteAll();
        itemRequestRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        User user2 = TestHelper.createUser(2L, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest itemRequest2 = TestHelper.createItemRequest(2L, 2L, new HashSet<>());
        ItemRequest save1 = itemRequestRepository.save(itemRequest1);
        ItemRequest save2 = itemRequestRepository.save(itemRequest2);

        //Действия
        Optional<List<ItemRequest>> actualItemRequestsExceptRequestor = itemRequestRepository
                .getItemRequestsCreatedAnotherUsers(1L, entityManager, 0, null);
        //Проверка
        assertThat(actualItemRequestsExceptRequestor.get().size(), Matchers.equalTo(1));
        assertThat(actualItemRequestsExceptRequestor.get().get(0).getRequestor(), Matchers.equalTo(2L));
    }

    @Test
    void getItemRequestsByRequestor_RequestorId_ReturnsItemRequests() {
        //Подготовка
        itemRequestRepository.deleteAll();
        itemRequestRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        User user2 = TestHelper.createUser(2L, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);

        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest itemRequest2 = TestHelper.createItemRequest(2L, 2L, new HashSet<>());
        ItemRequest save1 = itemRequestRepository.save(itemRequest1);
        ItemRequest save2 = itemRequestRepository.save(itemRequest2);

        int expectedQuantityOfItemRequests = 1;

        //Действия
        int actualQuantityOfItemRequests = itemRequestRepository.getItemRequestsByRequestor(1L).get().size();

        //Проверка
        assertThat(actualQuantityOfItemRequests, Matchers.equalTo(expectedQuantityOfItemRequests));
    }
}
