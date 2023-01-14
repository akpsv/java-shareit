package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;
import java.util.Optional;


interface ItemRequestService {
    Optional<ItemRequestDtoOut> addItemRequest(long requestorId, ItemRequestDtoIn itemRequestDtoIn);

    Optional<List<ItemRequestDtoOut>> getItemRequestsOfRequestor(long userId);

    Optional<List<ItemRequestDtoOut>> getItemRequestsCreatedAnotherUsers(long requestorId, Integer indexOfFirstElement, Integer numberOfElemenets);

    Optional<ItemRequestDtoOut> getItemRequestById(long userId, long requestId);
}
