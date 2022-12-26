package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDtoIn itemRequestDtoIn) {
        return ItemRequest.builder()
                .description(itemRequestDtoIn.getDescription())
                .requestor(itemRequestDtoIn.getRequestor())
                .items(new HashSet<>())
                .build();
    }

    public static ItemRequestDtoOut toItemRequestDtoOut(ItemRequest itemRequest) {
        Set<ItemRequestDtoOut.ItemForRequestDto> itemForRequestDtos = itemRequest.getItems().stream()
                .map(item -> ItemRequestDtoOut.ItemForRequestDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .ownerId(item.getOwnerId())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .requestId(item.getItemRequest().getId())
                        .build())
                .collect(Collectors.toSet());

        ItemRequestDtoOut resultDotOut = ItemRequestDtoOut.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .items(itemForRequestDtos)
                .created(itemRequest.getCreated())
                .build();
        return resultDotOut;
    }
}
