package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.error.ErrorHandler;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    @Mock
    BookingService stubBookingService;
    @InjectMocks
    BookingController bookingController;

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void add_BookingInDto_ReturnsBookingOutDto() throws Exception {
        //Подготовка
        long userID = 1L;
        BookingInDto bookingInDto = TestHelper.createBookingInDto(1L, 1L, BookingStatus.WAITING, 1L);
        BookingOutDto bookingOutDto = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        Mockito.when(stubBookingService.add(Mockito.anyLong(), Mockito.any())).thenReturn(Optional.of(bookingOutDto));
        //Действия и проверка
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(jsonPath("$.id").value(bookingOutDto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));

    }

    @Test
    void getBookingById_UserIdAndBookingId_ReturnsBookingOutDto() throws Exception {
        long userID = 1L;
        long bookingID = 1L;
        BookingOutDto bookingOutDto = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        Mockito.when(stubBookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(bookingOutDto));
        //Действия и проверка
        mvc.perform(get("/bookings/{bookingId}", bookingID)
                        .header("X-Sharer-User-Id", userID))
                .andExpect(jsonPath("$.id").value(bookingOutDto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getBookingsCurrentUser_DataOfUser_ReturnsBookingOutDtos() throws Exception {
        //Подготовка
        long userID = 1L;
        long bookingID = 1L;
        BookingOutDto bookingOutDto = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        Mockito.when(stubBookingService.getBookingsCurrentUser(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(List.of(bookingOutDto)));
        //Действия и проверка
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getBookingsCurrentUser_BookingStateNotExist_ThrowsEnumException() throws Exception {
        //Действия и проверка
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "StateNotExist")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsCurrentOwner() throws Exception {
        //Подготовка
        long userID = 1L;
        long bookingID = 1L;
        BookingOutDto bookingOutDto = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        Mockito.when(stubBookingService.getBookingsCurrentOwner(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(List.of(bookingOutDto)));
        //Действия и проверка
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void approveBooking() throws Exception {
        //Подготовка
        long userID = 1L;
        long bookingID = 1L;
        BookingOutDto bookingOutDto = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        Mockito.when(stubBookingService.approveBooking(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(Optional.of(bookingOutDto));
        //Действия и проверка
        mvc.perform(patch("/bookings/{bookingId}", bookingID)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}