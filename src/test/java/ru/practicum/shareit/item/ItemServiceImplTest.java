package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    UserRepository stubUserRepository;
    @Mock
    ItemRepository stubItemRepository;
    @Mock
    ItemRequestRepository stubItemRequestRepository;
    @Mock
    BookingRepository stubBookingRepository;
    @Mock
    CommentRepository stubCommentRepository;
    @PersistenceContext
    EntityManager entityManager;


    @Test
    void add_ItemDtoWithoutId_ReturnsItemDtoWithId() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        when(stubUserRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user1));

        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        when(stubItemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest1));

        Item item1 = TestHelper.createItem(1L, "вещь", 1L, true, null, new HashSet<>());
        Mockito.when(stubItemRepository.save(Mockito.any(Item.class))).thenReturn(item1);

        ItemService itemService = ItemServiceImpl.builder()
                .itemRepository(stubItemRepository)
                .userRepository(stubUserRepository)
                .itemRequestRepository(stubItemRequestRepository)
                .build();

        ItemDto newItemDto = TestHelper.createItemDto(0L, "item1", "description", true, 0);
        ItemDto expectedItemDto = TestHelper.createItemDto(1L, "item1", "description", true, 0);
        //Действия
        ItemDto actualItemDto = itemService.add(newItemDto, user1.getId()).get();

        //Проверка
        assertThat(actualItemDto.getId(), equalTo(expectedItemDto.getId()));
        assertThat(actualItemDto.getName(), equalTo(expectedItemDto.getName()));
        assertThat(actualItemDto.getDescription(), equalTo(expectedItemDto.getDescription()));
    }

    @Test
    void update_ItemDto_ReturnsItemDtoWithNewData() {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "вещь", 1L, true, null, new HashSet<>());
        when(stubItemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item1));
        when(stubItemRepository.save(Mockito.any(Item.class))).thenReturn(item1);

        ItemService itemService = ItemServiceImpl.builder()
                .itemRepository(stubItemRepository)
                .build();

        ItemDto newItemDto = TestHelper.createItemDto(item1.getId(), "item1new", "new description", true, 0);
        ItemDto expectedItemDto = newItemDto;

        //Действия
        ItemDto actualItemDto = itemService.update(newItemDto, newItemDto.getId(), 1L).get();
        //Проверка
        assertThat(actualItemDto.getId(), equalTo(expectedItemDto.getId()));
        assertThat(actualItemDto.getName(), equalTo(expectedItemDto.getName()));
        assertThat(actualItemDto.getDescription(), equalTo(expectedItemDto.getDescription()));
    }

    @Test
    void addComment_AddingCommentToItem_ReturnsCommentOutDto() {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "вещь", 1L, true, null, new HashSet<>());
        when(stubItemRepository.save(Mockito.any(Item.class))).thenReturn(item1);

        Booking booking1 = TestHelper.createBooking(1L, LocalDateTime.now().minusDays(1),BookingStatus.APPROVED, 1L, item1);
        when(stubBookingRepository.findByBookerIdEquals(anyLong())).thenReturn(Optional.of(List.of(booking1)));

        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        when(stubUserRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        Comment comment1 = TestHelper.createComment(1L, user1);
        when(stubCommentRepository.save(Mockito.any(Comment.class))).thenReturn(comment1);

        ItemService itemService = ItemServiceImpl.builder()
                .itemRepository(stubItemRepository)
                .bookingRepository(stubBookingRepository)
                .userRepository(stubUserRepository)
                .commentRepository(stubCommentRepository)
                .build();

        CommentOutDto expectedCommentOutDto = CommentOutDto.builder()
                .id(comment1.getId())
                .authorName(comment1.getAuthor().getName())
                .text(comment1.getText())
                .build();

        //Действия
        CommentOutDto actualCommentOutDto = itemService.addComment(1L, 1L, "some comment").get();
        //Проверка
        assertThat(actualCommentOutDto, samePropertyValuesAs(expectedCommentOutDto));
    }

    @Test
    void addComment_BlankComment_ThrowsBadRequestException() {
        //Подготовка
        ItemServiceImpl itemService = new ItemServiceImpl();
        String expectedMessage = "Пустое поле";
        //Действия
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () ->
                itemService.addComment(1L, 1L, "")
        );
        String actualMessage = badRequestException.getMessage();
        //Проверка
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void addComment_NotValidBookerId_ThrowEnitiyNotFoundException() {
        //Подготовка
        Mockito.when(stubBookingRepository.findByBookerIdEquals(Mockito.anyLong())).thenReturn(Optional.empty());
        ItemServiceImpl itemService = new ItemServiceImpl();
        itemService.setBookingRepository(stubBookingRepository);
        String expectedMessage = "Нельзя";
        //Действия
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                itemService.addComment(-1, 1, "comment")
        );
        String actualMessage = exception.getMessage();
        //Проверка
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getItemById_ItemId_ReturnsItem() {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, null, null);
        when(stubItemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        ItemServiceImpl itemService = new ItemServiceImpl();
        itemService.setItemRepository(stubItemRepository);

        Item expectedItem1 = TestHelper.createItem(1L, "item1", 1L, true, null, null);

        //Действия
        Item actualItem = itemService.getItemById(1L).get();
        //Проверка
        assertThat(actualItem, samePropertyValuesAs(expectedItem1));
    }

    @Test
    void getItemOutDtoById_ItemIdAndUserId_ReturnsItemOutDto() {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, new ItemRequest(), new HashSet<>());
        Mockito.when(stubItemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        ItemServiceImpl itemService = new ItemServiceImpl();
        itemService.setItemRepository(stubItemRepository);

        BookingOutDto bookingOutDto1 = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        BookingService stubBookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(stubBookingService.getBookingsCurrentOwner(anyLong(), Mockito.any(BookingState.class), isNull(), isNull()))
                .thenReturn(Optional.of(List.of(bookingOutDto1)));
        itemService.setBookingService(stubBookingService);

        ItemOutDto expectedItemOutDto = TestHelper.createItemOutDto(1L, "item1");

        //Действия
        ItemOutDto actualItemOutDto = itemService.getItemOutDtoById(1L, 1L).get();
        //Проверка
        assertThat(actualItemOutDto, samePropertyValuesAs(expectedItemOutDto, "lastBooking", "nextBooking"));
    }

    @Test
    void getItemOutDtoById_NotValidId_ThrowsEntityNotFoundException() {
        //Подготовка
        Mockito.when(stubItemRepository.findById(-1L)).thenReturn(Optional.empty());
        ItemServiceImpl itemService = new ItemServiceImpl();
        itemService.setItemRepository(stubItemRepository);
        String expectedMessage = "не найдена";
        //Действия
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItemOutDtoById(-1L, 1L)
        );
        String actualMessage = exception.getMessage();

        //Проверка
        assertTrue(actualMessage.contains(expectedMessage));
//        assertEquals(actualMessage, expectedMessage);

    }

    @Test
    void getOwnerItems_UserIdAndFromAndSize_CallsGetOwnerItemsFromItemRepository() {
        //Подготовка
        ItemServiceImpl itemService = new ItemServiceImpl();
        ItemRepository mockItemRepository = mock(ItemRepository.class);
        itemService.setItemRepository(mockItemRepository);

        //Действия
        itemService.getOwnerItems(1L, 0, 10);
        //Проверка
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .getOwnerItems(Mockito.any(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void searchItems_SearchingText_CallsSearchItemsFromItemRepository() {
        //Подготовка
        ItemServiceImpl itemService = new ItemServiceImpl();
        ItemRepository mockItemRepository = mock(ItemRepository.class);
        itemService.setItemRepository(mockItemRepository);
        //Действия
        itemService.searchItems("text", 0, 10);
        //Проверка
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .searchItems(Mockito.any(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
    }
}
