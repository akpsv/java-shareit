package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<ItemDto> add(ItemDto itemDto, long ownerId);

    Optional<ItemDto> update(ItemDto itemDto, long itemId, long userId);

    Optional<Item> getItemById(long itemId);

    Optional<ItemOutDto> getItemOutDtoById(long itemId, long userId);

    Optional<List<Item>> getOwnerItems(long userId, Integer from, Integer size);

    Optional<List<Item>> searchItems(String searchingText, Integer from, Integer size);

    Optional<CommentOutDto> addComment(long userId, long itemId, String comment);
}
