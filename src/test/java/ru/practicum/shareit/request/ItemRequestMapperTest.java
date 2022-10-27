package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ItemRequestMapperTest {

    Item item;
    ItemRequest itemRequst;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1)
                .name("вещь")
                .ownerId(1)
                .itemRequest(itemRequst)
                .build();

        Set<Item> items = new HashSet<>();
        items.add(item);

        itemRequst = ItemRequest.builder()
                .id(1L)
                .description("описание")
                .requestor(1L)
                .created(Date.from(Instant.now()))
                .items(items)
                .build();
    }

    @Test
    void toItemRequestDtoOut_PassCorrectItemRequst_GetCorrectItemRequestDtoOut() {
        //Подготовка
        ItemRequest baseItemRequst = itemRequst;

        //Действия
        ItemRequestDtoOut actualItemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(itemRequst);

        //Проверка
        assertThat(actualItemRequestDtoOut.getId(), equalTo(baseItemRequst.getId()));
        assertThat(actualItemRequestDtoOut.getRequestor(), equalTo(baseItemRequst.getRequestor()));
        assertThat(actualItemRequestDtoOut.getItems().iterator().next().getId(),
                equalTo(baseItemRequst.getItems().iterator().next().getId()));
    }
}