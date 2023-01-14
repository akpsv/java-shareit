package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    /**
     * POST /requests — добавить новый запрос вещи.
     * Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
     *
     * @param itemRequestDtoIn
     * @return
     */
    @PostMapping
    public ItemRequestDtoOut addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Validated({Create.class}) @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        return itemRequestService.addItemRequest(userId, itemRequestDtoIn).get();
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
    public List<ItemRequestDtoOut> getItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestsOfRequestor(userId).get();
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
    public List<ItemRequestDtoOut> getItemRequestsCreatedAnotherUsers(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                      @Validated({Create.class}) @RequestParam(required = false) @Min(0) Integer from,
                                                                      @Validated({Create.class}) @RequestParam(required = false) @Min(1) Integer size) {
        return itemRequestService.getItemRequestsCreatedAnotherUsers(userId, from, size).get();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getItemRequest(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable @Min(1) long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId).get();
    }

}
