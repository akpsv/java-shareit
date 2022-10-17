package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public Optional<ItemDto> toDto(final Item item) {
        return Optional.ofNullable(item)
                .map(processingItem -> ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .build()
                );
    }

    public Optional<Item> toItem(final ItemDto itemDto) {
        return Optional.ofNullable(itemDto)
                .map(processingItemDto -> Item.builder()
                        .name(itemDto.getName())
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .build()
                );
    }

    public Optional<ItemOutDto> toOutDto(Item item, BookingService bookingService) {
        BookingOutDto lastOutBookingDto = getLastBookingForItem(item, bookingService);
        ItemOutDto.BookingForItemDto lastBookingForItemDto = null;
        if (lastOutBookingDto != null) {
            lastBookingForItemDto = new ItemOutDto.BookingForItemDto(
                    lastOutBookingDto.getId(),
                    lastOutBookingDto.getStart(),
                    lastOutBookingDto.getEnd(),
                    lastOutBookingDto.getBookerId(),
                    lastOutBookingDto.getStatus()
            );
        }

        BookingOutDto nextOutBookingDto = getNextBookingForItem(item, bookingService);
        ItemOutDto.BookingForItemDto nextBookingForItemDto = null;
        if (nextOutBookingDto != null) {
            nextBookingForItemDto = new ItemOutDto.BookingForItemDto(
                    nextOutBookingDto.getId(),
                    nextOutBookingDto.getStart(),
                    nextOutBookingDto.getEnd(),
                    nextOutBookingDto.getBookerId(),
                    nextOutBookingDto.getStatus()
            );
        }

        Set<CommentOutDto> commentOutDtos = item.getComments().stream()
                .map(comment -> CommentMapper.toOutDto(comment))
                .collect(Collectors.toSet());

        ItemOutDto itemOutDto = ItemOutDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBookingForItemDto)
                .nextBooking(nextBookingForItemDto)
                .comments(commentOutDtos)
                .build();
        return Optional.of(itemOutDto);
    }

    private BookingOutDto getLastBookingForItem(Item item, BookingService bookingService) {
        BookingOutDto bookingOutDto1 = bookingService.getBookingsCurrentOwner(item.getOwnerId(), BookingState.PAST)
                .get().stream()
                .filter(bookingOutDto -> bookingOutDto.getItem().getId() == item.getId())
                .findFirst()
                .orElse(null);
        return bookingOutDto1;
    }

    private BookingOutDto getNextBookingForItem(Item item, BookingService bookingService) {
        BookingOutDto bookingOutDto1 = bookingService.getBookingsCurrentOwner(item.getOwnerId(), BookingState.FUTURE)
                .get().stream()
                .filter(bookingOutDto -> bookingOutDto.getItem().getId() == item.getId())
                .findFirst()
                .orElse(null);
        return bookingOutDto1;
    }
}
