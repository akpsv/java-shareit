package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    /**
     * POST /requests — добавить новый запрос вещи.
     * Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
     *
     * @param itemRequestDtoIn
     * @return
     */
    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Valid @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        return itemRequestClient.addItemRequest(userId, itemRequestDtoIn);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable @Min(1) long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    /**
     * GET /requests — получить список своих запросов вместе с данными об ответах на них.
     * Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
     * id вещи, название, id владельца.
     * Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
     * Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
     *
     * @param userId
     * @return
     */
    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    /**
     * GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
     * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
     * на которые они могли бы ответить.
     * Запросы сортируются по дате создания: от более новых к более старым.
     * Результаты должны возвращаться постранично.
     * Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0,
     * и size — количество элементов для отображения.
     *
     * @param userId
     * @param from
     * @param size
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsCreatedAnotherUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getItemRequestsCreatedAnotherUsers(userId, from, size);
    }
}
