package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.item.model.Item;

import java.util.Date;

/**
 * TODO Sprint add-bookings.
 * Чтобы воспользоваться нужной вещью, её требуется забронировать.
 * Бронирование, или Booking — ещё одна важная сущность приложения.
 * Бронируется вещь всегда на определённые даты.
 * Владелец вещи обязательно должен подтвердить бронирование.
 */

@Value
@Builder(toBuilder = true)
public class Booking {
    private int id;
    private Date start;
    private Date end;
    private Item item;
    private int booker; //Ид пользователя, который осуществляет бронирование
    private BookingStatus status;
}
