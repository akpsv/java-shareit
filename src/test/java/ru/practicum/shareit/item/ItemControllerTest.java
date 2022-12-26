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
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.CommentInDto;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
//@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private ObjectMapper mapper = new ObjectMapper();
    @Mock
    ItemService stubItemService;
    @InjectMocks
    ItemController itemController;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
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
    void add_OwnerIdNotExist_ThrowsNotFoundException() throws Exception {
        //Подготовка
        int userID = 1;
        ItemDto itemDtoOut = TestHelper.createItemDto(1L, "item1", "description", true, 1L);
        Mockito.when(stubItemService.add(Mockito.any(), Mockito.anyLong())).thenThrow(NotFoundException.class);
        //Действия и проверка
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOut))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isNotFound());
//                .andExpect(jsonPath("$.id").value(itemDtoOut.getId()))
//                .andExpect(jsonPath("$.name").value(itemDtoOut.getName()));
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

    @Test
    void search_SearchTextAndSizeOfPage_ReturnsItems() throws Exception {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, new ItemRequest(), new HashSet<>());
        Mockito
                .when(stubItemService.searchItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(List.of(item1)));
        //Действия и проверка
        mvc.perform(get("/items/search")
                        .param("text", "some test")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

    }

    @Test
    void getOwnerItems_UserIdAndSizeOfPage_ReturnsItemOutDtos() throws Exception {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, new ItemRequest(), new HashSet<>());
        Mockito
                .when(stubItemService.getOwnerItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(List.of(item1)));
        ItemOutDto itemOutDto = TestHelper.createItemOutDto(1L, "itemOutDto");
        Mockito.when(stubItemService.getItemOutDtoById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(itemOutDto));
        //Действия и проверка
        long userID = 1L;
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userID)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void addComment() throws Exception {
        long itemID = 1L;
        CommentInDto commentInDto = TestHelper.createCommentInDto(1L);
        Mockito.when(stubItemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenThrow(EntityNotFoundException.class);

        long userID = 1L;
        mvc.perform(post("/items/{itemId}/comment", itemID)
                        .content(mapper.writeValueAsString(commentInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isNotFound());
    }

    @Test
    void addComment_CommentInDto_ReturnsCommentOutDto() throws Exception {
        //Подготовка
        long itemID = 1L;
        CommentInDto commentInDto = TestHelper.createCommentInDto(1L);
        CommentOutDto commentOutDto = TestHelper.createCommentOutDto(1L);
        Mockito.when(stubItemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString())).thenReturn(Optional.of(commentOutDto));

        long userID = 1L;
        //Действия и проверка
        mvc.perform(post("/items/{itemId}/comment", itemID)
                        .header("X-Sharer-User-Id", userID)
                        .content(mapper.writeValueAsString(commentInDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}