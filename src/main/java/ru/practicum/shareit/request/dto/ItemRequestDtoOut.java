package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
public class ItemRequestDtoOut {
    private long id;
    private String description;
    private long requestor; //Ид пользователя, который создал запрос
    private Date created;
    private Set<ItemForRequestDto> items;

    public ItemRequestDtoOut(long id, String description, long requestor, Date created, Set<ItemForRequestDto> items) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
        this.items = items;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @Builder(toBuilder = true)
    public static class ItemForRequestDto {
        private final long id;
        private final String name;
        private final String description;
        private final long ownerId;
        private final boolean available;
        private final long requestId;
    }
}
