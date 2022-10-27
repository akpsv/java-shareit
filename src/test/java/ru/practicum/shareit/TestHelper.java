package ru.practicum.shareit;

import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class TestHelper {
    public static void createMocks(){
        UserRepository stubUserRepository = Mockito.mock(UserRepository.class);
        ItemRepository stubItemRepository = Mockito.mock(ItemRepository.class);
        ItemService itemService = ItemServiceImpl.builder()
                .itemRepository(stubItemRepository)
                .userRepository(stubUserRepository)
                .build();
    }
    public static Booking createBooking(long bookingId, BookingStatus status, long bookerId, Item item) {
        return Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .status(status)
                .bookerId(bookerId)
                .item(item)
                .build();
    }

    public static Item createItem(long itemId, long ownerId, boolean available, ItemRequest itemRequest, Set<Comment> comments) {
        return Item.builder()
                .id(itemId)
                .name("вещь")
                .description("описание")
                .ownerId(ownerId)
                .available(available)
                .itemRequest(itemRequest)
                .comments(comments)
                .build();
    }

    public static ItemRequest createItemRequest(long itemRequestId, long requestorId, Set<Item> items) {
        return ItemRequest.builder()
                .id(itemRequestId)
                .description("описание")
                .requestor(requestorId)
                .created(Date.from(Instant.now()))
                .items(items)
                .build();
    }

    public static UserDto createUserDto(long userId, String name, String email) {
        return UserDto.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
    }

    public static User createUser(long userId, String name, String email){
        return User.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
    }


    public static ItemDto createItemDto(long itemId, String name, String description, boolean available, long requestId) {
        return  ItemDto.builder()
                .id(itemId)
                .name("вещь")
                .description("описание")
                .available(available)
                .requestId(requestId)
                .build();
    }
}
