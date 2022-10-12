package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Repository
public class InMemoryItemStore implements ItemStore {
    private Map<Integer, Item> items = new HashMap<>();
    private int countOfId = 1;

    private int generateId() {
        return countOfId++;
    }

    @Override
    public Item add(Item item, final int ownerId) {
        Item itemWithId = item.toBuilder()
                .id(generateId())
                .ownerId(ownerId)
                .build();
        items.put(itemWithId.getId(), itemWithId);
        return itemWithId;
    }

    @Override
    public Item update(Item item) {
        Item updatedItem = items.get(item.getId()).toBuilder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        items.put(updatedItem.getId(), updatedItem);
        return updatedItem;
    }

    @Override
    public Map<Integer, Item> getItems() {
        return new HashMap<>(items);
    }

    @Override
    public <T, R> R get(Function<T, R> getFunc, T t) {
        return getFunc.apply(t);
    }
}
