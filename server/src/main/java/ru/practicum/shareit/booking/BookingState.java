package ru.practicum.shareit.booking;

import ru.practicum.shareit.error.EnumException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String value) {
        BookingState state;
        try {
            state = valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new EnumException(e);
        }
        return state;
    }
}
