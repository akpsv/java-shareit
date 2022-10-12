package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

//@RequiredArgsConstructor(onConstructor_ = @Autowired )
@SpringBootTest
public class ItemRequestServiceImpl_IntegrationTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void testAddItemRequestToDB() {
        //Подготовка
        User user1 = User.builder()
                .name("user1")
                .email("user1@email.ru")
                .build();
        userRepository.save(user1);

        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository);
        ItemRequestDtoIn dtoIn = ItemRequestDtoIn.builder()
                .description("Какой-то текст")
                .requestor(1L)
                .build();

        ItemRequestDtoOut expectedItemRequestDtoOut = ItemRequestDtoOut.builder()
                .id(1L)
                .description("Какой-то текст")
                .requestor(1L)
                .build();

        Optional<ItemRequestDtoOut> itemRequestDtoOut = itemRequestService.addItemRequest(1L, dtoIn);


        assertThat(itemRequestDtoOut.get().getDescription(), equalTo(expectedItemRequestDtoOut.getDescription()));


    }
}
