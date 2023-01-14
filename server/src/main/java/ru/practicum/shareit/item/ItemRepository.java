package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Item_;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    default Optional<List<Item>> searchItems(EntityManager entityManager, String searchingText, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root<Item> fromItems = cq.from(Item.class);
        cq.select(fromItems);

        Predicate predicateOr = cb.or(
                cb.like(cb.lower(fromItems.get(Item_.name)), "%" + searchingText.toLowerCase() + "%"),
                cb.like(cb.lower(fromItems.get(Item_.description)), "%" + searchingText.toLowerCase() + "%")
        );
        cq.where(cb.and(
                predicateOr,
                cb.isTrue(fromItems.get(Item_.available))
        ));

        TypedQuery<Item> query = entityManager.createQuery(cq);

        if (from != null && size != null) {
            query.setFirstResult(from).setMaxResults(size);
        }

        List<Item> items = query.getResultList();
        return Optional.of(items);
    }

    default Optional<List<Item>> getOwnerItems(EntityManager entityManager, long ownerId, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> cq = cb.createQuery(Item.class);
        Root<Item> fromItems = cq.from(Item.class);
        cq.select(fromItems).where(cb.equal(fromItems.get(Item_.ownerId), ownerId));
        TypedQuery<Item> typedQuery = entityManager.createQuery(cq);

        if (from != null && size != null) {
            typedQuery.setFirstResult(from).setMaxResults(size);
        }

        List<Item> ownerItems = typedQuery.getResultList();
        return Optional.of(ownerItems);
    }
}
