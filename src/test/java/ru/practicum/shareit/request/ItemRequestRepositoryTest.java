package ru.practicum.shareit.request;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    void getItemRequestsExceptRequestor_ThereAreSeveralItemRequest_GetAllRequestsExceptSpecificRequestorId() {
        //Подготовка
        User user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.ru")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@email.ru")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);


        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("описание")
                .requestor(1L)
                .created(Date.from(Instant.now()))
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L)
                .description("описание")
                .requestor(2L)
                .created(Date.from(Instant.now()))
                .build();
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

        //Действия
        Optional<List<ItemRequest>> actualItemRequestsExceptRequestor = itemRequestRepository
                .getItemRequestsCreatedAnotherUsers(1, entityManager, 0, 10);

        //Проверка
        assertThat(actualItemRequestsExceptRequestor.get().size(), Matchers.equalTo(1));
        assertThat(actualItemRequestsExceptRequestor.get().get(0).getRequestor(), Matchers.equalTo(2L));
    }
}