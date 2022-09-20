package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.CommentInDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingService bookingService;

    @PostMapping
    public Optional<Item> add(@Validated({Create.class}) @RequestBody final ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") final int userId) {
        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("Ид пользователя не найдено"));

        return Optional.ofNullable(itemDto).flatMap(dto -> itemService.add(dto, userId));
    }

    @PatchMapping("/{itemId}")
    public Optional<ItemDto> update(@RequestBody ItemDto itemDto, @PathVariable final int itemId, @RequestHeader("X-Sharer-User-Id") final int userId) {
        return Optional.ofNullable(itemDto).flatMap(dto -> itemService.update(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public Optional<ItemOutDto> getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") final int userId) {
        Optional<Item> itemById = itemService.getItemById(itemId);
        if (!itemById.isPresent()) {
            throw new EntityNotFoundException("Такая вещь не  найдена");
        }

        Optional<ItemOutDto> itemOutDto = itemMapper.toOutDto(itemById.get(), bookingService);
        if (!itemById.filter(item -> item.getOwnerId() == userId).isPresent()) {
            return itemOutDto.map(dto -> dto.toBuilder().lastBooking(null).nextBooking(null).build());
        }
        return itemOutDto;
    }

    @GetMapping
    public Optional<List<ItemOutDto>> getOwnerItems(@RequestHeader("X-Sharer-User-Id") final int userId) {
        List<Item> itemsOfOwner = itemService.getOwnerItems(userId).get().stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

        List<ItemOutDto> itemOutDtos = itemsOfOwner.stream()
                .map(item -> getItemById(item.getId(), userId))
                .map(Optional::get)
                .collect(Collectors.toList());
        return Optional.ofNullable(itemOutDtos);
    }

    @GetMapping("/search")
    public Optional<List<Item>> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public Optional<CommentOutDto> addComment(@RequestHeader("X-Sharer-User-Id") final int userId, @PathVariable long itemId, @RequestBody CommentInDto comment) {
        if (comment.getText().isBlank()) {
            throw new BadRequestException("Пустое поле comment");
        }
        Comment addedComment = itemService.addComment(userId, itemId, comment.getText());

        return Optional.ofNullable(CommentMapper.toOutDto(addedComment));
    }
}
