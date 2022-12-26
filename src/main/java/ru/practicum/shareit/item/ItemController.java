package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentInDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Min;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Validated({Create.class}) @RequestBody final ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") final int userId) {
        return itemService.add(itemDto, userId).get();
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable final int itemId, @RequestHeader("X-Sharer-User-Id") final int userId) {
        return itemService.update(itemDto, itemId, userId).get();
    }

    @GetMapping("/{itemId}")
    public ItemOutDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") final int userId) {
        return itemService.getItemOutDtoById(itemId, userId).get();
    }

    @GetMapping
    public List<ItemOutDto> getOwnerItems(@RequestHeader("X-Sharer-User-Id") final int userId,
                                          @RequestParam(required = false) @Min(0) Integer from,
                                          @RequestParam(required = false) @Min(1) Integer size) {
        List<Item> itemsOfOwner = itemService.getOwnerItems(userId, from, size).get().stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());

//        return itemsOfOwner.stream()
//                .map(item -> getItemById(item.getId(), userId))
//                .collect(Collectors.toList());
        return itemsOfOwner.stream()
                .map(item -> itemService.getItemOutDtoById(item.getId(), userId).get())
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text, @RequestParam(required = false) @Min(0) Integer from,
                             @RequestParam(required = false) @Min(1) Integer size) {
        return itemService.searchItems(text, from, size).get();
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDto addComment(@RequestHeader("X-Sharer-User-Id") final int userId, @PathVariable long itemId, @RequestBody CommentInDto comment) {
        return itemService.addComment(userId, itemId, comment.getText()).get();
    }
}
