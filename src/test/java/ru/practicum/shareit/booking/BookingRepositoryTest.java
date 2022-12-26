package ru.practicum.shareit.booking;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.flush();
        itemRequestRepository.deleteAll();
        itemRequestRepository.flush();
        itemRepository.deleteAll();
        itemRepository.flush();
        bookingRepository.deleteAll();
        bookingRepository.flush();
    }

    @Test
    void save_BookingWithoutId_BookingWithId() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@emial.ru");
        userRepository.save(user1);
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest1);
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, savedItemRequest1, new HashSet<>());
        Item savedItem1 = itemRepository.saveAndFlush(item1);
        Booking booking1 = TestHelper.createBooking(0L, LocalDateTime.now().plusMinutes(5), null, BookingStatus.WAITING, 1L, savedItem1);
        //Действия
        Booking actualBooking = bookingRepository.save(booking1);
        //Проверка
        assertThat(actualBooking.getId(), not(0L));
    }

    @ParameterizedTest
    @CsvSource({
            "WAITING, WAITING",
            "WAITING, ALL",
            "REJECTED, REJECTED",
            "WAITING, FUTURE"
    })
    void getBookingCurrentUser_SomeBookingState_ReturnsBookings(String bookingStatus, String bookingState) {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@emial.ru");
        userRepository.save(user1);
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest1);
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, savedItemRequest1, new HashSet<>());
        Item savedItem1 = itemRepository.saveAndFlush(item1);
        Booking booking1 = TestHelper.createBooking(0L, LocalDateTime.now().plusMinutes(5), null, BookingStatus.valueOf(bookingStatus), 1L, savedItem1);
        bookingRepository.save(booking1);

        int expectedQuantityOfBookings = 1;

        //Действия
        int actualQuantityOfBookings = bookingRepository.getBookingCurrentUser(entityManager, 1L, BookingState.valueOf(bookingState), 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(expectedQuantityOfBookings));
    }

    @ParameterizedTest
    @ArgumentsSource(BookingCurrentArgumentsProvider.class)
    void getBookingCurrentUser_BookingStateIsCurrent_ReturnsBookings(Booking testBookingWithDiffrentDateTime, BookingState bookingState) throws InterruptedException {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@emial.ru");
        userRepository.save(user1);
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest1);
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, savedItemRequest1, new HashSet<>());
        Item savedItem1 = itemRepository.saveAndFlush(item1);
        Booking booking1 = testBookingWithDiffrentDateTime.toBuilder().item(savedItem1).build();
        Booking savedBooking1 = bookingRepository.save(booking1);

        int expectedQuantityOfBookings = 1;

        Thread.sleep(10000);
        //Действия
        int actualQuantityOfBookings = bookingRepository.getBookingCurrentUser(entityManager, 1L, bookingState, 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(expectedQuantityOfBookings));
    }

    static class BookingCurrentArgumentsProvider implements ArgumentsProvider {
        public BookingCurrentArgumentsProvider() {
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of(TestHelper.createBooking(1L, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, 1L, new Item()), BookingState.CURRENT)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BookingPastArgumentsProvider.class)
    void getBookingCurrentUser_BookingStateIsPast_ReturnsBookings(Booking testBookingWithDiffrentDateTime, BookingState bookingState) throws InterruptedException {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@emial.ru");
        userRepository.save(user1);
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest1);
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, savedItemRequest1, new HashSet<>());
        Item savedItem1 = itemRepository.saveAndFlush(item1);
//        Booking booking1 = TestHelper.createBooking(0L, LocalDateTime.now().plusMinutes(5), null, BookingStatus.valueOf(bookingStatus), 1L, savedItem1);
        Booking booking1 = testBookingWithDiffrentDateTime.toBuilder().item(savedItem1).build();
        Booking savedBooking1 = bookingRepository.save(booking1);

        int expectedQuantityOfBookings = 1;

        Thread.sleep(10000);
        //Действия
        int actualQuantityOfBookings = bookingRepository.getBookingCurrentUser(entityManager, 1L, bookingState, 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(expectedQuantityOfBookings));
    }

    static class BookingPastArgumentsProvider implements ArgumentsProvider {
        public BookingPastArgumentsProvider() {
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
//                    Arguments.of(TestHelper.createBooking(1L, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, 1L, new Item()), BookingState.CURRENT),
                    Arguments.of(TestHelper.createBooking(1L, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusSeconds(7), BookingStatus.WAITING, 1L, new Item()), BookingState.PAST)
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
            "WAITING, WAITING",
            "WAITING, ALL",
            "REJECTED, REJECTED",
            "WAITING, FUTURE"
    })
    void getBookingCurrentOwner_SomeBookingStatusesAndBookingStats_ReturnsBookings(String bookingStatus, String bookingState) {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@emial.ru");
        userRepository.save(user1);
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest1);
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, savedItemRequest1, new HashSet<>());
        Item savedItem1 = itemRepository.saveAndFlush(item1);
        Booking booking1 = TestHelper.createBooking(0L, LocalDateTime.now().plusMinutes(5), null, BookingStatus.valueOf(bookingStatus), 1L, savedItem1);
        bookingRepository.save(booking1);

        int expectedQuantityOfBookings = 1;

        //Действия
        int actualQuantityOfBookings = bookingRepository.getBookingCurrentOwner(entityManager, 1L, BookingState.valueOf(bookingState), 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(expectedQuantityOfBookings));
    }

    @ParameterizedTest
    @ArgumentsSource(BookingCurrentArgumentsProvider.class)
    void getBookingCurrentOwner_BookingStateIsCurrent_ReturnsBookings(Booking testBookingWithDiffrentDateTime, BookingState bookingState) throws InterruptedException {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@emial.ru");
        userRepository.save(user1);
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest1);
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, savedItemRequest1, new HashSet<>());
        Item savedItem1 = itemRepository.saveAndFlush(item1);
        Booking booking1 = testBookingWithDiffrentDateTime.toBuilder().item(savedItem1).build();
        Booking savedBooking1 = bookingRepository.save(booking1);

        int expectedQuantityOfBookings = 1;

        Thread.sleep(10000);
        //Действия
        int actualQuantityOfBookings = bookingRepository.getBookingCurrentOwner(entityManager, 1L, bookingState, 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(expectedQuantityOfBookings));
    }

//    static class BookingCurrentArgumentsProvider implements ArgumentsProvider {
//        public BookingCurrentArgumentsProvider() {
//        }
//
//        @Override
//        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
//            return Stream.of(
//                    Arguments.of(TestHelper.createBooking(1L, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, 1L, new Item()), BookingState.CURRENT)
//            );
//        }
//    }

    @ParameterizedTest
    @ArgumentsSource(BookingPastForOwnerArgumentsProvider.class)
    void getBookingCurrentOwner_BookingStateIsPast_ReturnsBookings(Booking testBookingWithDiffrentDateTime, BookingState bookingState) throws InterruptedException {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@emial.ru");
        userRepository.save(user1);
        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        ItemRequest savedItemRequest1 = itemRequestRepository.save(itemRequest1);
        Item item1 = TestHelper.createItem(1L, "item1", 1L, true, savedItemRequest1, new HashSet<>());
        Item savedItem1 = itemRepository.saveAndFlush(item1);
        Booking booking1 = testBookingWithDiffrentDateTime.toBuilder().item(savedItem1).build();
        Booking savedBooking1 = bookingRepository.save(booking1);

        int expectedQuantityOfBookings = 1;

        Thread.sleep(10000);
        //Действия
        int actualQuantityOfBookings = bookingRepository.getBookingCurrentOwner(entityManager, 1L, bookingState, 0, 10).get().size();
        //Проверка
        assertThat(actualQuantityOfBookings, Matchers.equalTo(expectedQuantityOfBookings));
    }

    static class BookingPastForOwnerArgumentsProvider implements ArgumentsProvider {
        public BookingPastForOwnerArgumentsProvider() {
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
//                    Arguments.of(TestHelper.createBooking(1L, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusDays(1), BookingStatus.WAITING, 1L, new Item()), BookingState.CURRENT),
                    Arguments.of(TestHelper.createBooking(1L, LocalDateTime.now().plusSeconds(5), LocalDateTime.now().plusSeconds(7), BookingStatus.WAITING, 1L, new Item()), BookingState.PAST)
            );
        }
    }

}
