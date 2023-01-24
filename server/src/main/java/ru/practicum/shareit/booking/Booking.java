package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Чтобы воспользоваться нужной вещью, её требуется забронировать.
 * Бронирование, или Booking — ещё одна важная сущность приложения.
 * Бронируется вещь всегда на определённые даты.
 * Владелец вещи обязательно должен подтвердить бронирование.
 */

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @Column(name = "booker_id")
    private long bookerId; //Ид пользователя, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    protected Item item;
}
