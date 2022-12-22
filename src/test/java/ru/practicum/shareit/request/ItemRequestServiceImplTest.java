package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository stubItemRequestRepository;
    @Mock
    UserRepository stubUserRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    void addItemRequest_RequestorIdIsZero_BadRequestException() {
        //Подготовка
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl();
        itemRequestService.setUserRepository(stubUserRepository);
        ItemRequestDtoIn itemRequestDtoIn = TestHelper.createItemRequestDtoIn(1L);

        Mockito.when(stubUserRepository.findById(0L)).thenReturn(Optional.empty());

        //Действия
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () ->
                itemRequestService.addItemRequest(0, itemRequestDtoIn)
        );

        //Проверка
        assertEquals("Запрашивающий пользователь не найден", notFoundException.getMessage());
    }

    @Test
    void addItemRequest_RequestorIdIsNotZero_GetItemRequestDtoOut() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequestDtoIn itemRequestDtoIn = TestHelper.createItemRequestDtoIn(1L);
        ItemRequestDtoOut itemRequestDtoOut = TestHelper.createItemRequestDtoOut(1L, 1L);

        Mockito.when(stubUserRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(stubItemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest1);

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(stubItemRequestRepository,
                stubUserRepository, entityManager);

        ItemRequestDtoOut expectedItemRequestDtoOut = itemRequestDtoOut;

        //Действия
        Optional<ItemRequestDtoOut> actualItemRequestDtoOut = itemRequestService.addItemRequest(1, itemRequestDtoIn);

        //Проверка
        assertThat(actualItemRequestDtoOut.get().getId(), equalTo(expectedItemRequestDtoOut.getId()));
        assertThat(actualItemRequestDtoOut.get().getDescription(), equalTo(expectedItemRequestDtoOut.getDescription()));
        assertThat(actualItemRequestDtoOut.get().getRequestor(), equalTo(expectedItemRequestDtoOut.getRequestor()));
    }

    @Test
    void getItemRequestById_ItemRequestId_ReturnsItemRequestDtoOut() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());

        Mockito.when(stubUserRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(stubItemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest1));

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(stubItemRequestRepository,
                stubUserRepository, entityManager);

        //Действия
        Optional<ItemRequestDtoOut> actualItemRequestById = itemRequestService.getItemRequestById(1L, 1L);
        //Проверка
        assertThat(actualItemRequestById, not(Optional.empty()));
    }

    @Test
    void getItemRequestsOfRequestor_ItemRequestorId_ReturnsItemRequests() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());

        Mockito.when(stubUserRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(stubItemRequestRepository.getItemRequestsByRequestor(Mockito.anyLong())).thenReturn(Optional.of(List.of(itemRequest1)));

        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(stubItemRequestRepository,
                stubUserRepository, entityManager);

        int expectedQuanttyOfItemRequests = 1;

        //Действия
        int actualQuantityOfItemRequests = itemRequestService.getItemRequestsOfRequestor(1L).get().size();
        //Проверка
        assertThat(actualQuantityOfItemRequests, equalTo(expectedQuanttyOfItemRequests));
    }

    @Test
    void getItemRequestsCreatedAnotherUsers_WhenCall_CallsGetItemRequestsCreatedAnotherUsersFromItemRequestRepository() {
        //Подготовка
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());

        ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        Mockito.when(mockItemRequestRepository.getItemRequestsCreatedAnotherUsers(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(Collections.emptyList()));
        ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl();
        itemRequestService.setItemRequestRepository(mockItemRequestRepository);

        //Действия
        itemRequestService.getItemRequestsCreatedAnotherUsers(1L, 0, 10);
        //Проверка
        Mockito.verify(mockItemRequestRepository, Mockito.times(1))
                .getItemRequestsCreatedAnotherUsers(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

    }
}