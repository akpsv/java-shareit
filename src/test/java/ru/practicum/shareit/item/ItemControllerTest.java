package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    ItemService stubItemService;
    @InjectMocks
    ItemController itemController;

    ObjectMapper mapper = new ObjectMapper();

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
    }

    @Test
    void add_ItemDtoAndUserId_ReturnsUserDto() throws Exception {
        //Подготовка
        int userID = 1;
        ItemDto itemDtoOut = TestHelper.createItemDto(1L, "item1", "description", true, 1L);
        Mockito.when(stubItemService.add(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(itemDtoOut));
        //Действия и проверка
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoOut.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoOut.getName()));
    }

    @Test
    void update_ItemDtoAndUserIdAndItemId_ReturnsItemDto() throws Exception {
        //Подготовка
        int userID = 1;
        long itemID = 1L;
        ItemDto itemDtoIn = TestHelper.createItemDto(1L, "item1", "description", true, 1L);
        ItemDto itemDtoOut = TestHelper.createItemDto(1L, "item1updated", "description", true, 1L);
        Mockito.when(stubItemService.update(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(itemDtoOut));
        //Действия и проверка
        mvc.perform(patch("/items/{itemId}", itemID)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoOut.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoOut.getName()));
    }

    @Test
    void getItemById_ItemIdAndUserId_ReturnsItemOutDto() throws Exception {
        //Подготовка
        int userID = 1;
        long itemID = 1L;
        ItemOutDto itemOutDto = TestHelper.createItemOutDto(1L, "item1");
        Mockito.when(stubItemService.getItemOutDtoById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(itemOutDto));
        //Действия и проверка
        mvc.perform(get("/items/{itemId}", itemID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemOutDto.getId()))
                .andExpect(jsonPath("$.name").value(itemOutDto.getName()));
    }
}