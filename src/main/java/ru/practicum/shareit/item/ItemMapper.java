package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public static Optional<ItemDto> toDto(final Item item) {
        long itemRequestId;
        if (item.getItemRequest() != null) {
            itemRequestId = item.getItemRequest().getId();
        } else {
            itemRequestId = 0;
        }

        return Optional.ofNullable(item)
                .map(processingItem -> ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .requestId(itemRequestId)
                        .build()
                );
    }

    public static Optional<Item> toItem(final ItemDto itemDto, ItemRequestRepository itemRequestRepository) {
        Optional<ItemRequest> resultItemRequest = itemRequestRepository.findById(itemDto.getRequestId());
        ItemRequest itemRequest;
        if (resultItemRequest.isPresent()) {
            itemRequest = resultItemRequest.get();
        } else {
            itemRequest = null;
        }
        return Optional.ofNullable(itemDto)
                .map(processingItemDto -> Item.builder()
                        .name(itemDto.getName())
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .itemRequest(itemRequest)
                        .build()
                );
    }

    public static Optional<ItemOutDto> toOutDto(Item item, BookingService bookingService) {
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

    private static BookingOutDto getLastBookingForItem(Item item, BookingService bookingService) {
        return bookingService.getBookingsCurrentOwner(item.getOwnerId(), BookingState.PAST, null, null)
                .get().stream()
                .filter(bookingOutDto -> bookingOutDto.getItem().getId() == item.getId())
                .findFirst()
                .orElse(null);
    }

    private static BookingOutDto getNextBookingForItem(Item item, BookingService bookingService) {
        return bookingService.getBookingsCurrentOwner(item.getOwnerId(), BookingState.FUTURE, null, null)
                .get().stream()
                .filter(bookingOutDto -> bookingOutDto.getItem().getId() == item.getId())
                .findFirst()
                .orElse(null);
    }
}
