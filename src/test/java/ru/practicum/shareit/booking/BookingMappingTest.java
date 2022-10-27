package ru.practicum.shareit.booking;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;


class BookingMappingTest {
    Booking booking1;
    BookingOutDto expectedBookingOutDto;
    Item item1;
    ItemRequest itemRequest;
    ItemRepository mockItemRepository;
    UserService mockUserService;
    UserDto userDto1;
    BookingInDto bookingInDto1;

    @BeforeEach
    void setUp() {
        item1 = Item.builder()
                .id(1)
                .name("вещь")
                .description("описание")
                .ownerId(1L)
                .available(true)
                .itemRequest(itemRequest)
                .build();
        Set<Item> items = new HashSet<>();
        items.add(item1);

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("описание")
                .requestor(1L)
                .created(Date.from(Instant.now()))
                .items(items)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .status(BookingStatus.WAITING)
                .bookerId(1L)
                .item(item1)
                .build();

        expectedBookingOutDto = BookingOutDto.builder()
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
                .id(1L)
                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .build();

        userDto1 = UserDto.builder()
                .id(1L)
                .name("BookerName")
                .email("bookername@mail.ru")
                .build();

        bookingInDto1 = BookingInDto.builder()
                .id(1L)
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .itemId(1L)
                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .build();

        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockUserService = Mockito.mock(UserService.class);

    }

    @Test
    void toBookingOutDto_CorrectBooking_CorrectlyFilledBookingOutDto() {
        //Подготовка
        Mockito.when(mockUserService.getUserById(anyLong())).thenReturn(Optional.of(userDto1));
        BookingMapping bookingMapping = new BookingMapping(mockItemRepository, mockUserService);

        //Действия
        BookingOutDto actualBookinOutDto = bookingMapping.toBookingOutDto(booking1);

        //Проверка
        assertThat(expectedBookingOutDto, Matchers.samePropertyValuesAs(actualBookinOutDto, "booker", "item"));
        assertThat(expectedBookingOutDto.getItem(), Matchers.samePropertyValuesAs(actualBookinOutDto.getItem()));
        assertThat(expectedBookingOutDto.getBooker(), Matchers.samePropertyValuesAs(actualBookinOutDto.getBooker()));
    }

    @Test
    void toBooking_BookingInDto_CorrectlyFillBooking() {
        //Подготовка
        Mockito.when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        BookingMapping bookingMapping = new BookingMapping(mockItemRepository, mockUserService);

        Booking expectedBooking = booking1;
        //Действия

        Booking actualBooking = bookingMapping.toBooking(bookingInDto1);
        //Проверка
        assertThat(expectedBooking, Matchers.samePropertyValuesAs(actualBooking));
    }
}