package ru.practicum.shareit.booking;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    Booking booking1;
    User user1;
    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@email.ru")
                .build();

        booking1 = Booking.builder()
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void save_BookingWithoutId_BookingWithId(){
        //Подготовка
        userRepository.save(user1);

        //Действия
        Booking actualBooking = bookingRepository.save(booking1);
        //Проверка
        assertThat(actualBooking.getId(), equalTo(1L));
    }

    @Test
    void findByBookerIdEquals() {
    }

    @Test
    void getBookingCurrentUser() {
    }

    @Test
    void getBookingCurrentOwner() {
    }
}