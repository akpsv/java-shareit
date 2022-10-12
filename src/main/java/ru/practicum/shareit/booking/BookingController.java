package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingInDto bookingInDto) {
        return bookingService.add(userId, bookingInDto).get();
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId).get();
    }

    @GetMapping
    public List<BookingOutDto> getBookingsCurrentUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @Validated @RequestParam(required = false) @Min(0) Integer from,
                                                      @Validated @RequestParam(required = false) @Min(1) Integer size) {

        return bookingService.getBookingsCurrentUser(userId, BookingState.from(state), from, size).get();
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getBookingsCurrentOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @Validated @RequestParam(required = false) @Min(0) Integer from,
                                                       @Validated @RequestParam(required = false) @Min(1) Integer size) {

        return bookingService.getBookingsCurrentOwner(ownerId, BookingState.from(state), from, size).get();
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long bookingId, @RequestParam boolean approved) {

        return bookingService.approveBooking(userId, bookingId, approved).get();
    }
}
