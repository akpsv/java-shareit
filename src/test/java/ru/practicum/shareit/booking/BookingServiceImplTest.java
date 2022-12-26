package ru.practicum.shareit.booking;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.error.ItemNotAvailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingServiceImplTest {
    @Test
    void add_BookingInDto_ReternsBookingDtoOut() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        UserRepository stubUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        Item item1 = TestHelper.createItem(1L, "item", 2L, true, null, new HashSet<>());
        Booking booking1 = TestHelper.createBooking(1L, LocalDateTime.now(), null, BookingStatus.WAITING, 1L, item1);
        BookingRepository stubBookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(stubBookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking1);

        BookingOutDto bookingOutDto1 = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        BookingMapping stubBookingMapping = Mockito.mock(BookingMapping.class);
        Mockito.when(stubBookingMapping.toBooking(Mockito.any(BookingInDto.class))).thenReturn(booking1);
        Mockito.when(stubBookingMapping.toBookingOutDto(Mockito.any(Booking.class))).thenReturn(bookingOutDto1);

        BookingServiceImpl bookingService = new BookingServiceImpl();
        bookingService.setBookingMapping(stubBookingMapping);
        bookingService.setBookingRepository(stubBookingRepository);
        bookingService.setUserRepository(stubUserRepository);

        BookingInDto bookingInDto1 = TestHelper.createBookingInDto(1L, 1L, BookingStatus.WAITING, 1L);

        BookingOutDto expectedBookingOutDto = bookingOutDto1;

        //Действия
        BookingOutDto actualBookingDtoOut = bookingService.add(1L, bookingInDto1).get();

        //Проверка
        assertThat(expectedBookingOutDto, Matchers.samePropertyValuesAs(actualBookingDtoOut, "booker", "item"));
        assertThat(expectedBookingOutDto.getBooker(), Matchers.samePropertyValuesAs(actualBookingDtoOut.getBooker()));
        assertThat(expectedBookingOutDto.getItem(), Matchers.samePropertyValuesAs(actualBookingDtoOut.getItem()));
    }

    @Test
    void add_ItemNotAvailable_ThrowsItemNotAvailableException() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        UserRepository stubUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        Item item1 = TestHelper.createItem(1L, "item", 2L, false, null, new HashSet<>());
        Booking booking1 = TestHelper.createBooking(1L, LocalDateTime.now(), null, BookingStatus.WAITING, 1L, item1);
        BookingRepository stubBookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(stubBookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking1);

        BookingOutDto bookingOutDto1 = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        BookingMapping stubBookingMapping = Mockito.mock(BookingMapping.class);
        Mockito.when(stubBookingMapping.toBooking(Mockito.any(BookingInDto.class))).thenReturn(booking1);
        Mockito.when(stubBookingMapping.toBookingOutDto(Mockito.any(Booking.class))).thenReturn(bookingOutDto1);

        BookingServiceImpl bookingService = new BookingServiceImpl();
        bookingService.setBookingMapping(stubBookingMapping);
        bookingService.setBookingRepository(stubBookingRepository);
        bookingService.setUserRepository(stubUserRepository);

        BookingInDto bookingInDto1 = TestHelper.createBookingInDto(1L, 1L, BookingStatus.WAITING, 1L);

        //Действия
        ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class, () ->
                bookingService.add(1L, bookingInDto1)
        );
        //Проверка
        assertTrue(exception.getMessage().contains("недоступна"));
    }

    @Test
    void approveBooking_BookingStatusWatingAndArgumentApprovedIsTrue__BookingOutDtoWithChangedStatusToApproved() {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, null, new HashSet<>());
        Booking bookingWithStatusIsWating = TestHelper.createBooking(1L, LocalDateTime.now(), null, BookingStatus.WAITING, 1L, item1);
        Booking bookingWithStatusIsApproved = TestHelper.createBooking(1L, LocalDateTime.now(), null, BookingStatus.APPROVED, 1L, item1);
        BookingRepository stubBookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(stubBookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bookingWithStatusIsWating));
        Mockito.when(stubBookingRepository.save(Mockito.any(Booking.class))).thenReturn(bookingWithStatusIsApproved);

        UserDto userDto1 = TestHelper.createUserDto(1L, "user1", "user1@email.ru");
        UserService stubUserService = Mockito.mock(UserService.class);
        Mockito.when(stubUserService.getUserById(Mockito.anyLong())).thenReturn(Optional.of(userDto1));

        BookingMapping bookingMapping = new BookingMapping(null, stubUserService);

        //Создание тестируемого класса и добавление в него заглушек
        BookingServiceImpl bookingService = new BookingServiceImpl();
        bookingService.setBookingRepository(stubBookingRepository);
        bookingService.setBookingMapping(bookingMapping);

        //Действия
        BookingOutDto actualBookingOutDto = bookingService.approveBooking(1L, 1L, true).get();
        //Проверка
        assertThat(actualBookingOutDto.getStatus(), Matchers.equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingsCurrentUser_UserIdAndBookingState_ReturnsBookingsOfCurrentUser() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        UserRepository stubUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        Item item1 = TestHelper.createItem(1L, "item", 2L, true, null, new HashSet<>());
        Booking booking1 = TestHelper.createBooking(1L, LocalDateTime.now(), null, BookingStatus.WAITING, 1L, item1);
        BookingRepository stubBookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(stubBookingRepository.getBookingCurrentUser(
                        Mockito.isNull(),
                        Mockito.anyLong(),
                        Mockito.any(BookingState.class),
                        Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(List.of(booking1)));

        BookingOutDto bookingOutDto1 = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        BookingMapping stubBookingMapping = Mockito.mock(BookingMapping.class);
        Mockito.when(stubBookingMapping.toBookingOutDto(Mockito.any(Booking.class))).thenReturn(bookingOutDto1);

        BookingServiceImpl bookingService = new BookingServiceImpl();
        bookingService.setBookingMapping(stubBookingMapping);
        bookingService.setBookingRepository(stubBookingRepository);
        bookingService.setUserRepository(stubUserRepository);

        //Действия
        int actualQuantityOfBookings = bookingService.getBookingsCurrentUser(1L, BookingState.WAITING, 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(1));

    }

    @Test
    void getBookingsCurrentOwner() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        UserRepository stubUserRepository = Mockito.mock(UserRepository.class);
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        Item item1 = TestHelper.createItem(1L, "item", 2L, true, null, new HashSet<>());
        Booking booking1 = TestHelper.createBooking(1L, LocalDateTime.now(), null, BookingStatus.WAITING, 1L, item1);
        BookingRepository stubBookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(stubBookingRepository.getBookingCurrentOwner(
                        Mockito.isNull(),
                        Mockito.anyLong(),
                        Mockito.any(BookingState.class),
                        Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.of(List.of(booking1)));

        BookingOutDto bookingOutDto1 = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        BookingMapping stubBookingMapping = Mockito.mock(BookingMapping.class);
        Mockito.when(stubBookingMapping.toBookingOutDto(Mockito.any(Booking.class))).thenReturn(bookingOutDto1);

        BookingServiceImpl bookingService = new BookingServiceImpl();
        bookingService.setBookingMapping(stubBookingMapping);
        bookingService.setBookingRepository(stubBookingRepository);
        bookingService.setUserRepository(stubUserRepository);

        //Действия
        int actualQuantityOfBookings = bookingService.getBookingsCurrentOwner(2L, BookingState.WAITING, 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(1));
    }

    @Test
    void testGetBookingById_BookingId_ReturnsBookingOutDto() {
        //Подготовка
//        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
//        UserRepository stubUserRepository = Mockito.mock(UserRepository.class);
//        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        Item item1 = TestHelper.createItem(1L, "item", 2L, true, null, new HashSet<>());
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));

        Booking booking1 = TestHelper.createBooking(1L, LocalDateTime.now(), null, BookingStatus.WAITING, 1L, item1);
        BookingRepository stubBookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(stubBookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking1));

        BookingOutDto bookingOutDto1 = TestHelper.createBookingOutDto(1L, 1L, BookingStatus.WAITING);
        BookingMapping stubBookingMapping = Mockito.mock(BookingMapping.class);
//        Mockito.when(stubBookingMapping.toBooking(Mockito.any(BookingInDto.class))).thenReturn(booking1);
        Mockito.when(stubBookingMapping.toBookingOutDto(Mockito.any(Booking.class))).thenReturn(bookingOutDto1);

        BookingServiceImpl bookingService = new BookingServiceImpl();
        bookingService.setBookingMapping(stubBookingMapping);
        bookingService.setBookingRepository(stubBookingRepository);
//        bookingService.setUserRepository(stubUserRepository);

        //Действия
        Optional<BookingOutDto> actualOptionalWithBooking = bookingService.getBookingById(1L, 1L);
        //Проверка
        assertThat(actualOptionalWithBooking, Matchers.not(Optional.empty()));
    }
}
