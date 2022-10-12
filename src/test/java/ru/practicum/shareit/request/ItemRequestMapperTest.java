package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ItemRequestMapperTest {

    @Test
    void toItemRequestDtoOut_CorrectItemRequst_ReturnsCorrectItemRequestDtoOut() {
        //Подготовка
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, itemRequest1, new HashSet<>());
        itemRequest1 = itemRequest1.toBuilder().items(Set.of(item1)).build();

        ItemRequest expectedItemRequst = itemRequest1;

        //Действия
        ItemRequestDtoOut actualItemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(itemRequest1);

        //Проверка
        assertThat(actualItemRequestDtoOut.getId(), equalTo(expectedItemRequst.getId()));
        assertThat(actualItemRequestDtoOut.getRequestor(), equalTo(expectedItemRequst.getRequestor()));
        assertThat(actualItemRequestDtoOut.getItems().iterator().next().getId(),
                equalTo(expectedItemRequst.getItems().iterator().next().getId()));
    }
}
