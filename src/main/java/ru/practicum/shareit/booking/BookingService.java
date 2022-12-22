package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Optional<BookingOutDto> add(long userId, BookingInDto bookingInDto);

    Optional<BookingOutDto> approveBooking(long userId, long bookingId, boolean approved);

    Optional<BookingOutDto> getBookingById(long userId, long bookingId);

    Optional<List<BookingOutDto>> getBookingsCurrentUser(long userId, BookingState bookingState, Integer from, Integer size);

    Optional<List<BookingOutDto>> getBookingsCurrentOwner(long userId, BookingState bookingState, Integer from, Integer size);
}
