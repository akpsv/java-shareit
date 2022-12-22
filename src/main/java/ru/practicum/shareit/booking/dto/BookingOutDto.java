package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class BookingOutDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemForBookingDto item;
    private BookerDto booker;
    private long bookerId;
    private BookingStatus status;

    @Getter
    @Setter
    public static class ItemForBookingDto {
        long id;
        String name;
        String description;
        boolean available;

        public ItemForBookingDto(long id, String name, String description, boolean available) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.available = available;
        }
    }

    @Getter
    @Setter
    public static class BookerDto {
        long id;
        String name;
        String email;

        public BookerDto(long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}


