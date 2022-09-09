package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public Optional<Item> add(@Validated({Create.class}) @RequestBody final ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") final int userId) {
        userService.getUserById(userId).orElseThrow(() -> new NotFoundException("Ид пользователя не найдено"));
        return Optional.ofNullable(itemDto)
                .flatMap(dto -> itemService.add(dto, userId));
    }

    @PatchMapping("/{itemId}")
    public Optional<Item> update(@RequestBody ItemDto itemDto, @PathVariable final int itemId, @RequestHeader("X-Sharer-User-Id") final int userId) {
        return Optional.ofNullable(itemDto)
                .flatMap(dto -> itemService.update(itemDto, itemId, userId));
    }

    @GetMapping("/{itemId}")
    public Optional<Item> getItemById(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Optional<List<Item>> getOwnerItems(@RequestHeader("X-Sharer-User-Id") final int userId) {
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public Optional<List<Item>> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
