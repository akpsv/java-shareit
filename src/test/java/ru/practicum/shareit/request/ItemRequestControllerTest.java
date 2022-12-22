package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    ItemRequestService stubItemRequestService;
    @InjectMocks
    ItemRequestController itemRequestController;

    ObjectMapper mapper = new ObjectMapper();
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();
    }

    @Test
    void addItemRequest() throws Exception {
        //Подготовка
        long userID = 1L;
        ItemRequestDtoIn itemRequestDtoIn = TestHelper.createItemRequestDtoIn(1L);
        ItemRequestDtoOut itemRequestDtoOut = TestHelper.createItemRequestDtoOut(1L, 1L);
        Mockito.when(stubItemRequestService.addItemRequest(Mockito.anyLong(), Mockito.any())).thenReturn(Optional.of(itemRequestDtoOut));

        //Действия и проверка
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestor").value(1L));
    }

    @Test
    void getItemRequests() throws Exception {
        //Подготовка
        long userID = 1L;
        ItemRequestDtoOut itemRequestDtoOut = TestHelper.createItemRequestDtoOut(1L, 1L);
        Mockito.when(stubItemRequestService.getItemRequestsOfRequestor(Mockito.anyLong())).thenReturn(Optional.of(List.of(itemRequestDtoOut)));

        //Действия и проверка
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getItemRequestsCreatedAnotherUsers() {
    }

    @Test
    void getItemRequest() throws Exception {
        //Подготовка
        long userID = 1L;
        ItemRequestDtoOut itemRequestDtoOut = TestHelper.createItemRequestDtoOut(1L, 1L);
        Mockito.when(stubItemRequestService.getItemRequestById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(itemRequestDtoOut));

        //Действия и проверка
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestor").value(1L));
    }
}
