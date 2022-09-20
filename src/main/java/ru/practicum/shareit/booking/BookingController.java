package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private BookingMapping bookingMapping;

    @PostMapping
    public Optional<Booking> add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingInDto bookingInDto) {
        return bookingService.add(userId, bookingInDto);
    }

    @GetMapping("/{bookingId}")
    public Optional<BookingOutDto> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        BookingOutDto bookingOutDto = bookingMapping.toDto(bookingService.getBookingById(userId, bookingId).get());
        return Optional.of(bookingOutDto);
    }

    @GetMapping
    public Optional<List<BookingOutDto>> getBookingsCurrentUser(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(defaultValue = "ALL") String state) {
        userService.getUserById(userId).orElseThrow(() -> new EntityNotFoundException("ИД пользователя не правильный"));

        BookingState bookingState = BookingState.from(state);
        Optional<List<Booking>> bookingsCurrentUser = bookingService.getBookingsCurrentUser(userId, bookingState);
        List<BookingOutDto> bookingOutDtos = bookingsCurrentUser.get().stream().map(booking -> bookingMapping.toDto(booking)).collect(Collectors.toList());

        return Optional.of(bookingOutDtos);
    }

    @GetMapping("/owner")
    public Optional<List<BookingOutDto>> getBookingsCurrentOwner(@RequestHeader("X-Sharer-User-Id") long ownerId, @RequestParam(defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        Optional<List<BookingOutDto>> bookingsCurrentOwner = bookingService.getBookingsCurrentOwner(ownerId, bookingState);

        return bookingsCurrentOwner;
    }

    @PatchMapping("/{bookingId}")
    public Optional<BookingOutDto> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId, @RequestParam boolean approved) {
        Optional<Booking> booking = bookingService.approveBooking(userId, bookingId, approved);

        return Optional.of(bookingMapping.toDto(booking.get()));
    }
}
