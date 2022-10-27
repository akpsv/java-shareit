package ru.practicum.shareit.booking;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class BookingServiceImplIntegrationTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingMapping bookingMapping;


//    Booking booking1;
//    Booking booking2;
//    BookingOutDto expectedBookingOutDto;
//    Item item1;
//    Item item2;
//    ItemRequest itemRequest;
//    ItemRequest itemRequest2;
//    UserDto userDto1;
//    UserDto userDto2;
//    BookingInDto bookingInDto1;
//    BookingInDto bookingInDto2;

//    @BeforeEach
//    void setUp() {
//        item1 = Item.builder()
//                .id(1)
//                .name("вещь")
//                .description("описание")
//                .ownerId(1L)
//                .available(true)
//                .itemRequest(itemRequest)
//                .build();
//        Set<Item> items = new HashSet<>();
//        items.add(item1);
//
//        item2 = Item.builder()
//                .id(2)
//                .name("вещь")
//                .description("описание")
//                .ownerId(2L)
//                .available(true)
//                .itemRequest(itemRequest2)
//                .build();
//        Set<Item> items2 = new HashSet<>();
//        items2.add(item2);
//
//        itemRequest = ItemRequest.builder()
//                .id(1L)
//                .description("описание")
//                .requestor(1L)
//                .created(Date.from(Instant.now()))
//                .items(items)
//                .build();
//        itemRequest2 = ItemRequest.builder()
//                .id(2L)
//                .description("описание")
//                .requestor(2L)
//                .created(Date.from(Instant.now()))
//                .items(items2)
//                .build();
//
//        booking1 = Booking.builder()
//                .id(1L)
//                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
//                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
//                .status(BookingStatus.WAITING)
//                .bookerId(1L)
//                .item(item1)
//                .build();
//        booking2 = Booking.builder()
//                .id(2L)
//                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
//                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
//                .status(BookingStatus.WAITING)
//                .bookerId(2L)
//                .item(item2)
//                .build();
//
//        expectedBookingOutDto = BookingOutDto.builder()
//                .booker(new BookingOutDto.BookerDto(
//                        1L,
//                        "BookerName",
//                        "bookername@mail.ru")
//                )
//                .item(new BookingOutDto.ItemForBookingDto(
//                        1L,
//                        "вещь",
//                        "описание",
//                        true
//                ))
//                .id(1L)
//                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
//                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
//                .bookerId(1L)
//                .status(BookingStatus.WAITING)
//                .build();
//
//        userDto1 = UserDto.builder()
//                .id(1L)
//                .name("BookerName")
//                .email("bookername@mail.ru")
//                .build();
//        userDto2 = UserDto.builder()
//                .id(2L)
//                .name("BookerName2")
//                .email("bookername2@mail.ru")
//                .build();
//
//        bookingInDto1 = BookingInDto.builder()
//                .id(1L)
//                .bookerId(1L)
//                .status(BookingStatus.WAITING)
//                .itemId(1L)
//                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
//                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
//                .build();
//        bookingInDto2 = BookingInDto.builder()
//                .id(2L)
//                .bookerId(2L)
//                .status(BookingStatus.WAITING)
//                .itemId(2L)
//                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
//                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
//                .build();
//
//        itemRepository.save(item1);
//        itemRepository.save(item2);
//        userService.addUser(userDto1);
//        userService.addUser(userDto2);
//        bookingService.add(1L, bookingInDto2);
//        bookingService.add(2L, bookingInDto1);
//
//    }

    /**
     * статические фабричные методы создающие различные объекты
     */
    static Booking createBooking(long bookingId, BookingStatus status, long bookerId, Item item) {
        return Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.of(2023, 01, 10, 12, 00))
                .end(LocalDateTime.of(2023, 02, 10, 12, 00))
                .status(status)
                .bookerId(bookerId)
                .item(item)
                .build();
    }

    static Item createItem(long itemId, long ownerId, boolean available, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemId)
                .name("вещь")
                .description("описание")
                .ownerId(ownerId)
                .available(available)
                .itemRequest(itemRequest)
                .build();
    }

    static ItemRequest createItemRequest(long itemRequestId, long requestorId, Set<Item> items) {
        return ItemRequest.builder()
                .id(itemRequestId)
                .description("описание")
                .requestor(requestorId)
                .created(Date.from(Instant.now()))
                .items(items)
                .build();
    }

    static UserDto createUserDto(long userId) {
        return UserDto.builder()
                .id(1L)
                .name("BookerName" + userId)
                .email("bookername" + userId + "@mai.ru")
                .build();
    }

    @Test
    @Ignore
    void add_AddingBookingToDB_ReternsBookingDtoOut() {
        //Подготовка
        //Действия
//        BookingOutDto actualBookingDtoOut = bookingService.add(1L, bookingInDto1).get();
//
//        //Проверка
//        assertThat(expectedBookingOutDto, Matchers.samePropertyValuesAs(actualBookingDtoOut, "booker", "item"));
//        assertThat(expectedBookingOutDto.getBooker(), Matchers.samePropertyValuesAs(actualBookingDtoOut.getBooker()));
//        assertThat(expectedBookingOutDto.getItem(), Matchers.samePropertyValuesAs(actualBookingDtoOut.getItem()));
    }

    //Сделать параметризованный тест
    @Test
    void checkAndSetBookingStatus_WhenCalled_ChangeBookingStatus() {
        //Подготовка

        //Действия

        //Проверка
    }

    /**
     *
     */
    @Test
    void approveBooking_BookingStatusWatingAndArgumentApprovedIsTrue__BookingOutDtoWithChangedStatusToApproved() {

        //Подготовка
        //Создание возращаемых объектов из методов заглушки
        Item item1 = createItem(1L, 1L, true, null);
        Booking booking1 = createBooking(1L, BookingStatus.WAITING, 1L, item1);
        ItemRequest itemRequest1 = createItemRequest(1L, 1L, Set.of(item1));
        item1 = item1.toBuilder().itemRequest(itemRequest1).build();
        itemRequest1 = itemRequest1.toBuilder().items(Set.of(item1)).build();
        UserDto userDto1 = createUserDto(1L);

        //Создание и настройка заглушки для BookingRepository
//        BookingRepository stubBookingRepository = Mockito.mock(BookingRepository.class);
//        Mockito.when(stubBookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking1));
//        Mockito.when(stubBookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking1);
        //Создание и настройка заглушки для BookingMapping
//        UserService stubUserService = Mockito.mock(UserService.class);
//        Mockito.when(stubUserService.getUserById(Mockito.anyLong())).thenReturn(Optional.of(userDto1));
//        BookingMapping bookingMapping = new BookingMapping(null, stubUserService);
        //Создание тестируемого класса и добавление в него заглушки
//        BookingServiceImpl bookingService = new BookingServiceImpl();
//        bookingService.setBookingRepository(stubBookingRepository);
//        bookingService.setBookingMapping(bookingMapping);

        userService.addUser(userDto1);
        itemRepository.save(item1);
        bookingRepository.save(booking1);
        itemRequestRepository.save(itemRequest1);
        //Действия
        BookingOutDto actualBookingOutDto = bookingService.approveBooking(1L, 1L, true).get();
        //Проверка
        assertThat(actualBookingOutDto.getStatus(), Matchers.equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getBookingsCurrentUser() {
    }

    @Test
    void getBookingsCurrentOwner() {
    }
}