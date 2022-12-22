package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

@AllArgsConstructor
@Component
public class BookingMapping {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public BookingOutDto toBookingOutDto(Booking booking) {
        UserDto userDto = userService.getUserById(booking.getBookerId()).get();
        return BookingOutDto.builder()
                .booker(new BookingOutDto.BookerDto(
                        userDto.getId(),
                        userDto.getName(),
                        userDto.getEmail())
                )
                .item(new BookingOutDto.ItemForBookingDto(
                        booking.getItem().getId(),
                        booking.getItem().getName(),
                        booking.getItem().getDescription(),
                        booking.getItem().getAvailable()
                ))
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBookerId())
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(BookingInDto bookingInDto) {
        Item item = itemRepository.findById(bookingInDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с таким ИД отсутствует"));
        return Booking.builder()
                .id(bookingInDto.getId())
                .item(item)
                .start(bookingInDto.getStart())
                .end(bookingInDto.getEnd())
                .bookerId(bookingInDto.getBookerId())
                .status(bookingInDto.getStatus())
                .build();
    }
}
