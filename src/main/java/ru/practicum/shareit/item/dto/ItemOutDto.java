package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.Set;

@Value
@Builder(toBuilder = true)
public class ItemOutDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingOutDto lastBooking;
    private BookingOutDto nextBooking;
    private Set<CommentOutDto> comments;

}
