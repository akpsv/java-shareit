package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public class ItemMapper {
    public static Optional<ItemDto> toDto(final Item item) {
        return Optional.ofNullable(item)
                .map(processingItem -> ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .build()
                );
    }

    public static Optional<Item> toItem(final ItemDto itemDto) {
        return Optional.ofNullable(itemDto)
                .map(processingItemDto -> Item.builder()
                        .name(itemDto.getName())
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .build()
                );
    }
}
