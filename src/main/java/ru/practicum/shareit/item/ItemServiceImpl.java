package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStore itemStore;

    @Autowired
    public ItemServiceImpl(ItemStore itemStore) {
        this.itemStore = itemStore;
    }

    @Override
    public Optional<Item> add(ItemDto itemDto, int ownerId) {
        return ItemMapper.toItem(itemDto)
                .map(item -> itemStore.add(item, ownerId));
    }

    /**
     * Обновление вещи информацией из dto если вещь принадлежит пользователю с соответствующим ид
     *
     * @param itemDto - объект содержит данные вещи
     * @param itemId  - ид вещи
     * @param userId  - ид владельца вещи
     * @return - вещь или ничего
     */
    @Override
    public Optional<Item> update(final ItemDto itemDto, final int itemId, final int userId) {
        getItemById(itemId).filter(item -> item.getOwnerId() == userId)
                .orElseThrow(() -> new NotFoundException("Ид владельца не правильный"));

        return Optional.of(itemDto)
                .flatMap(dto -> getItemById(itemId)
                        .map(item -> updateItemFromDto(dto, item))
                        .flatMap(updatingItem -> Optional.of(itemStore.update(updatingItem)))
                );
    }

    private Item updateItemFromDto(ItemDto dto, Item item) {
        Item updatingItem = item;
        if (dto.getName() != null) {
            updatingItem = updatingItem.toBuilder().name(dto.getName()).build();
        }
        if (dto.getDescription() != null) {
            updatingItem = updatingItem.toBuilder().description(dto.getDescription()).build();
        }
        if (dto.getAvailable() != null) {
            updatingItem = updatingItem.toBuilder().available(dto.getAvailable()).build();
        }
        return updatingItem;
    }

    @Override
    public Optional<Item> getItemById(final int itemId) {
        Function<Integer, Item> getItemById = (idOfItem -> itemStore.getItems().get(idOfItem));
        return Optional.ofNullable(itemStore.get(getItemById, itemId));
    }

    @Override
    public Optional<List<Item>> getOwnerItems(final int userId) {
        Function<Integer, List<Item>> getOwnerItems = (idOfUser ->
                itemStore.getItems().entrySet().stream()
                        .map(Map.Entry::getValue)
                        .filter(value -> value.getOwnerId() == idOfUser)
                        .collect(Collectors.toList())
        );
        return Optional.of(itemStore.get(getOwnerItems, userId));
    }

    @Override
    public Optional<List<Item>> searchItems(String searchingText) {
        Function<String, List<Item>> searchedItems = (str -> {
            if (str.isBlank()) {
                return Collections.emptyList();
            }
            return itemStore.getItems().values().stream()
                    .filter(item -> item.getName().toLowerCase().contains(str.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(str.toLowerCase()))
                    .filter(item -> item.getAvailable())
                    .collect(Collectors.toList());
        });
        return Optional.ofNullable(itemStore.get(searchedItems, searchingText));
    }
}
