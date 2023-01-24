package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.error.EnumException;

import java.util.Optional;

public enum BookingState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;

    public static Optional<BookingState> from(String value) {
        BookingState state;
        try {
            state = valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EnumException(e);
        }
        return Optional.of(state);
    }
}
