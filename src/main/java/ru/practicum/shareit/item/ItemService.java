package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Optional<Item> add(ItemDto itemDto, int ownerId);

    Optional<Item> update(ItemDto itemDto, int itemId, int userId);

    Optional<Item> getItemById(int itemId);

    Optional<List<Item>> getOwnerItems(int userId);

    Optional<List<Item>> searchItems(String searchingText);
}
