package ru.practicum.shareit;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TestHelper {

    public static Booking createBooking(long bookingId, LocalDateTime startDateTime, BookingStatus status, long bookerId, Item item) {
        return Booking.builder()
                .id(bookingId)
//                .start(LocalDateTime.of(2022, 01, 10, 12, 00))
                .start(startDateTime)
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .status(status)
                .bookerId(bookerId)
                .item(item)
                .build();
    }

    public static Item createItem(long itemId, String name, long ownerId, boolean available, ItemRequest itemRequest, Set<Comment> comments) {
        return Item.builder()
                .id(itemId)
                .name(name)
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

    public static User createUser(long userId, String name, String email) {
        return User.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
    }


    public static ItemDto createItemDto(long itemId, String name, String description, boolean available, long requestId) {
        return ItemDto.builder()
                .id(itemId)
                .name("вещь")
                .description("описание")
                .available(available)
                .requestId(requestId)
                .build();
    }

    public static Comment createComment(long commentId, User author) {
        return Comment.builder()
                .id(commentId)
                .text("comment text")
                .author(author)
                .build();
    }

    public static BookingOutDto createBookingOutDto(long id, long bookerId, BookingStatus status) {
        return BookingOutDto.builder()
                .booker(new BookingOutDto.BookerDto(
                        1L,
                        "BookerName",
                        "bookername@mail.ru")
                )
                .item(new BookingOutDto.ItemForBookingDto(
                        1L,
                        "вещь",
                        "описание",
                        true
                ))
                .id(id)
                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .bookerId(bookerId)
                .status(status)
                .build();
    }

    public static BookingInDto createBookingInDto(long id, long bookerId, BookingStatus status, long itemId) {
        return BookingInDto.builder()
                .id(id)
                .bookerId(bookerId)
                .status(status)
                .itemId(itemId)
                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .build();

    }

    public static ItemOutDto createItemOutDto(Long itemId, String name) {
        return ItemOutDto.builder()
                .id(itemId)
                .name(name)
                .description("описание")
                .lastBooking(null)
                .nextBooking(null)
                .available(true)
                .comments(new HashSet<>())
                .build();
    }

    public static ItemRequestDtoIn createItemRequestDtoIn(Long requestorId) {
        return ItemRequestDtoIn.builder()
                .description("описание")
                .requestor(requestorId)
                .build();
    }

    public static ItemRequestDtoOut createItemRequestDtoOut(Long itemRequestId, Long requestorId) {
        return ItemRequestDtoOut.builder()
                .id(itemRequestId)
                .description("описание")
                .requestor(requestorId)
                .build();
    }
}
