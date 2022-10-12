package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.ItemNotAvailableException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingMapping bookingMapping;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Optional<Booking> add(long bookerId, BookingInDto bookingInDto) {
        Booking booking = bookingMapping.fromInDto(bookingInDto);
        checkBooking(bookerId, booking);

        booking = booking.toBuilder().status(BookingStatus.WAITING).bookerId(bookerId).build();
        Optional<Booking> addedBooking = Optional.of(bookingRepository.save(booking));

        return addedBooking;
    }

    @Transactional
    @Override
    public Optional<Booking> approveBooking(long userId, long bookingId, boolean approved) {
        /**
         * Подтверждение или отклонение запроса на бронирование. Может быть выполнено только владельцем вещи.
         * Затем статус бронирования становится либо APPROVED, либо REJECTED.
         * Эндпоинт — PATCH /bookings/{bookingId}?approved={approved}, параметр approved может принимать значения true или false.
         */
        Booking resultBooking = bookingRepository.findById(bookingId)   //Получение бронирования по идентификатору
                .filter(booking -> booking.getItem().getOwnerId() == userId) //является ли запрашивающий пользователь владельцем?
                .flatMap(booking -> {
                    if (approved) {
                        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                            throw new BadRequestException("Статус бронирования уже установлен в одобрено.");
                        }
                        booking = booking.toBuilder().status(BookingStatus.APPROVED).build();
                    } else {
                        booking = booking.toBuilder().status(BookingStatus.REJECTED).build();
                    }
                    Booking savedBooking = bookingRepository.save(booking);
                    return Optional.of(savedBooking);
                })
                .orElseThrow(() -> new EntityNotFoundException("Элемент не найден."));

        return Optional.ofNullable(resultBooking);
    }

    @Override
    public Optional<Booking> getBookingById(long userId, long bookingId) {
        Booking resultBooking = bookingRepository.findById(bookingId)
                .filter(booking -> booking.getBookerId() == userId ||
                        itemRepository.findById(booking.getItem().getId()).get().getOwnerId() == userId)
                .orElseThrow(() -> new EntityNotFoundException("Такое бронирование не найдено."));

        return Optional.ofNullable(resultBooking);
    }

    @Override
    public Optional<List<Booking>> getBookingsCurrentUser(long userId, BookingState bookingState) {
        return bookingRepository.getBookingCurrentUser(entityManager, userId, bookingState);
    }

    @Transactional
    @Override
    public Optional<List<BookingOutDto>> getBookingsCurrentOwner(long userId, BookingState bookingState) {
        Optional<List<Booking>> bookingCurrentOwner = bookingRepository.getBookingCurrentOwner(entityManager, userId, bookingState);
        List<BookingOutDto> bookingOutDtos = bookingCurrentOwner.get().stream().map(booking -> bookingMapping.toDto(booking)).collect(Collectors.toList());

        return Optional.ofNullable(bookingOutDtos);
    }

    private Booking checkBooking(long userId, Booking booking) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким ИД не найден"));
        if (userId == booking.getItem().getOwnerId()) {
            throw new NotFoundException("Владелец не может забронировать вещь.");
        }

        Optional.ofNullable(booking.getItem()).filter(item -> item.getAvailable()).orElseThrow(() -> new ItemNotAvailableException("Статус вещи - недоступна для бронирования"));
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ConstraintViolationException("Дата окончания наступает ранее даты начала", null);
        }
        return booking;
    }
}