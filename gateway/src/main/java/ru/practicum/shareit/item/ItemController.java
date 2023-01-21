package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") final int userId, @RequestBody final ItemDtoIn itemDtoIn) {
        log.info("Creating item {} for userId={}", itemDtoIn, userId);
        return itemClient.addItem(userId, itemDtoIn);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") final int userId) {
        log.info("Get item with itemId={} for userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader("X-Sharer-User-Id") final int userId,
                                                @RequestParam(required = false) @Min(0) Integer from,
                                                @RequestParam(required = false) @Min(1) Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable final int itemId, @RequestHeader("X-Sharer-User-Id") final int userId, @RequestBody ItemDtoIn itemDtoIn) {
        return itemClient.update(itemId, userId, itemDtoIn);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text, @RequestParam(required = false) @Min(0) Integer from,
                                         @RequestParam(required = false) @Min(1) Integer size) {
        log.info("Search text {} ", text);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") final int userId, @PathVariable long itemId, @RequestBody CommentDtoIn comment) {
        return itemClient.addComment(userId, itemId, comment);
    }
}
