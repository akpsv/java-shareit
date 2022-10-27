package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TODO:
 *   Требования:
 *   1. Проверить входные данные
 *       Набор сценариев тестирования требования:
 *          Первый аргумент (long requestorId)
 *              * 1.1 Когда идентификатор пользователя равен нулю(т.е. в тои числе пользователь не существует) должно выбрасываться исключение
 *              * 1.2 Когда идентификатор пользователя равен 1 (т.е. пользователь существует) возвращается ItemRequestDtoOut с данными пользователя
 *          Второй аргумент(ItemRequestDtoIn itemRequestDtoIn)
 *              * 1.2 Проверить что поля входящего ДТО правильно заполенеы:
 *              *  - Когда поле description пустое или null то выбрасывается исключение
 * *
 * 2. Проверить что данные правильно сохраняются в БД (состояние БД меняется)
 * 2.1 Проверить, что после сохранения в БД возвращается объект с правильно заполнеными полями
 */
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    ItemRequestDtoIn itemRequestDtoIn;
    ItemRequestDtoOut itemRequestDtoOut;
    User user1;
    @Mock
    ItemRequestRepository mockItemRequestRepository;
    @Mock
    UserRepository mockUserRepository;
    ItemRequestService itemRequestServiceWithMocks;

    @BeforeEach
    void setUp() {
        itemRequestServiceWithMocks = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository);
        itemRequestDtoIn = ItemRequestDtoIn.builder()
                .description("Какой-то текст")
                .requestor(1L)
                .build();

        itemRequestDtoOut = ItemRequestDtoOut.builder()
                .id(1L)
                .description("описание")
                .requestor(1L)
                .build();

        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.ru")
                .build();

    }

    @Test
    void addItemRequest_RequestorIdIsZero_BadRequestException() {
        //Подготовка
        Mockito.when(mockUserRepository.findById(0L)).thenReturn(Optional.empty());

        //Действия
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                itemRequestServiceWithMocks.addItemRequest(0, itemRequestDtoIn)
        );

        //Проверка
        assertEquals("Запрашивающий пользователь не найден", notFoundException.getMessage());
    }

    @Test
    void addItemRequest_RequestorIdIsNotZero_GetItemRequestDtoOut() {
        //Подготовка
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("описание")
                .requestor(1L)
                .created(Date.from(Instant.now()))
                .build();

        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequestDtoOut expectedItemRequestDtoOut = itemRequestDtoOut;
        //Действия
        Optional<ItemRequestDtoOut> actualItemRequestDtoOut = itemRequestServiceWithMocks.addItemRequest(1, itemRequestDtoIn);

        //Проверка
        assertThat(actualItemRequestDtoOut.get().getId(), equalTo(expectedItemRequestDtoOut.getId()));
        assertThat(actualItemRequestDtoOut.get().getDescription(), equalTo(expectedItemRequestDtoOut.getDescription()));
        assertThat(actualItemRequestDtoOut.get().getRequestor(), equalTo(expectedItemRequestDtoOut.getRequestor()));
    }
}