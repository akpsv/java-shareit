package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Optional<Booking> add(long userId, BookingInDto bookingInDto);

    Optional<Booking> approveBooking(long userId, long bookingId, boolean approved);

    Optional<Booking> getBookingById(long userId, long bookingId);

    Optional<List<Booking>> getBookingsCurrentUser(long userId, BookingState bookingState);

    Optional<List<BookingOutDto>> getBookingsCurrentOwner(long userId, BookingState bookingState);
}
