package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;
import java.util.function.Function;

public interface ItemStore {

    Item add(Item item, int ownerId);

    Item update(Item item);

    Map<Integer, Item> getItems();

    <T, R> R get(Function<T, R> getFunc, T t);
}
