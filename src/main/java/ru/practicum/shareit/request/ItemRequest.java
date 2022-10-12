package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 * Ещё одна сущность, которая вам понадобится, — запрос вещи ItemRequest.
 * Пользователь создаёт запрос, если нужная ему вещь не найдена при поиске.
 * В запросе указывается, что именно он ищет.
 * В ответ на запрос другие пользовали могут добавить нужную вещь.
 */

@Value
@Builder(toBuilder = true)
public class ItemRequest {
    private int id;
    private String description;
    private int requestor; //Ид пользователя, который создал запрос
    private LocalDate created;
}
