package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class ItemOutDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private Set<CommentOutDto> comments;

    @Getter
    @Setter
    public static class BookingForItemDto {
        long id;
        LocalDateTime start;
        LocalDateTime end;
        long bookerId;
        BookingStatus status;

        public BookingForItemDto(long id, LocalDateTime start, LocalDateTime end, long bookerId, BookingStatus status) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.bookerId = bookerId;
            this.status = status;
        }
    }
}
