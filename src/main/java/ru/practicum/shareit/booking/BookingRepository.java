package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<List<Booking>> findByBookerIdEquals(long bookerId);

    default Optional<List<Booking>> getBookingCurrentUser(EntityManager em, long userId, BookingState state, Integer from, Integer size) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<Booking> fromBookings = criteriaQuery.from(Booking.class);
        //TODO: Заменить строковые обозначения имён свойст на использование статической метамодели
        Predicate predicate = null;
        switch (state.name()) {
            case "ALL":
                predicate = criteriaBuilder.equal(fromBookings.get("bookerId"), userId);
                break;
            case "CURRENT":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(fromBookings.get("bookerId"), userId),
                        criteriaBuilder.lessThan(fromBookings.<LocalDateTime>get("start"), LocalDateTime.now()),
                        criteriaBuilder.greaterThan(fromBookings.<LocalDateTime>get("end"), LocalDateTime.now()));
                break;
            case "PAST":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(fromBookings.get("bookerId"), userId),
                        criteriaBuilder.lessThan(fromBookings.<LocalDateTime>get("end"), LocalDateTime.now()));
                break;
            case "FUTURE":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(fromBookings.get("bookerId"), userId),
                        criteriaBuilder.greaterThan(fromBookings.<LocalDateTime>get("start"), LocalDateTime.now()));
                break;
            case "WAITING":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(fromBookings.get("bookerId"), userId),
                        criteriaBuilder.equal(fromBookings.<BookingStatus>get("status"), BookingStatus.WAITING));
                break;
            case "REJECTED":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(fromBookings.get("bookerId"), userId),
                        criteriaBuilder.equal(fromBookings.<BookingStatus>get("status"), BookingStatus.REJECTED));
                break;
        }

        criteriaQuery.select(fromBookings).where(predicate).orderBy(criteriaBuilder.desc(fromBookings.get("start")));
        TypedQuery<Booking> query = em.createQuery(criteriaQuery);
        if (from != null || size != null) {
            query.setFirstResult(from).setMaxResults(size);
        }
        List<Booking> resultList = query.getResultList();

        return Optional.of(resultList);
    }

    default Optional<List<Booking>> getBookingCurrentOwner(EntityManager em, long userId, BookingState state, Integer from, Integer size) {
        //Получить количество вещей которыми владеет пользователь
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Item> fromItems = criteriaQuery.from(Item.class);
        criteriaQuery.select(criteriaBuilder.count(fromItems));
        criteriaQuery.where(criteriaBuilder.equal(fromItems.get("ownerId"), userId));

        Long countItemsOfOwner = em.createQuery(criteriaQuery).getSingleResult();

        if (countItemsOfOwner == 0) {
            throw new NotFoundException("Запрашивающий пользователь не владеет вещами.");
        }

        //Основной запрос. Получить бронирования дла владельца в соответствии с условиями.
        CriteriaQuery<Booking> criteriaQueryFromBooking = criteriaBuilder.createQuery(Booking.class);
        Root<Booking> fromBookings = criteriaQueryFromBooking.from(Booking.class);
        Join<Booking, Item> bookingItemJoin = fromBookings.join("item");
        Predicate predicate = null;
        switch (state.name()) {
            case "ALL":
                predicate = criteriaBuilder.equal(bookingItemJoin.get("ownerId"), userId);
                break;
            case "CURRENT":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(bookingItemJoin.get("ownerId"), userId),
                        criteriaBuilder.lessThan(fromBookings.<LocalDateTime>get("start"), LocalDateTime.now()),
                        criteriaBuilder.greaterThan(fromBookings.<LocalDateTime>get("end"), LocalDateTime.now()));
                break;
            case "PAST":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(bookingItemJoin.get("ownerId"), userId),
                        criteriaBuilder.lessThan(fromBookings.<LocalDateTime>get("end"), LocalDateTime.now()));
                break;
            case "FUTURE":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(bookingItemJoin.get("ownerId"), userId),
                        criteriaBuilder.greaterThan(fromBookings.<LocalDateTime>get("start"), LocalDateTime.now()));
                break;
            case "WAITING":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(bookingItemJoin.get("ownerId"), userId),
                        criteriaBuilder.equal(fromBookings.<BookingStatus>get("status"), BookingStatus.WAITING));
                break;
            case "REJECTED":
                predicate = criteriaBuilder.and(
                        criteriaBuilder.equal(bookingItemJoin.get("ownerId"), userId),
                        criteriaBuilder.equal(fromBookings.<BookingStatus>get("status"), BookingStatus.REJECTED));
                break;
        }

        criteriaQueryFromBooking.select(fromBookings).where(predicate).orderBy(criteriaBuilder.desc(fromBookings.get("start")));
        TypedQuery<Booking> queryFromBookings = em.createQuery(criteriaQueryFromBooking);

        if (from != null || size != null) {
            queryFromBookings.setFirstResult(from).setMaxResults(size);
        }

        List<Booking> resultList = queryFromBookings.getResultList();

        return Optional.ofNullable(resultList);
    }
}
