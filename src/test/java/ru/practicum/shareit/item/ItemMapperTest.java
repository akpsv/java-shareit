package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.isNull;

class ItemMapperTest {

    @Test
    void toDto() {
    }

    @Test
    void toItem() {
    }

    @Test
    void toOutDto_ItemAndBookingService_ReturnsItemOutDto() {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, new ItemRequest(), new HashSet<>());
        BookingOutDto bookingOutDto1 = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        BookingService stubBookingService = Mockito.mock(BookingService.class);
        Mockito
                .when(stubBookingService.getBookingsCurrentOwner(anyLong(), Mockito.any(BookingState.class), isNull(), isNull()))
                .thenReturn(Optional.of(List.of(bookingOutDto1)));

        ItemOutDto expectedItemOutDto = TestHelper.createItemOutDto(1L, "item1");

        //Действия
        ItemOutDto actualItemOutDto = ItemMapper.toOutDto(item1, stubBookingService).get();

        //Проверка
        assertThat(actualItemOutDto, samePropertyValuesAs(expectedItemOutDto, "lastBooking", "nextBooking"));
    }
}
