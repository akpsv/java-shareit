package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Optional<List<ItemRequest>> getItemRequestsByRequestor(long requestorId);

    default Optional<List<ItemRequest>> getItemRequestsCreatedAnotherUsers(long requestorId, EntityManager entityManager,
                                                                           Integer indexOfFirstElement, Integer numberOfElemenets ) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ItemRequest> criteriaQuery = criteriaBuilder.createQuery(ItemRequest.class);

        Root<ItemRequest> fromItemRequest = criteriaQuery.from(ItemRequest.class); //Выборка
        criteriaQuery.select(fromItemRequest);
        criteriaQuery.where(criteriaBuilder.notEqual(fromItemRequest.get(ItemRequest_.requestor), requestorId));
        criteriaQuery.orderBy(criteriaBuilder.desc(fromItemRequest.get(ItemRequest_.created)));

        TypedQuery<ItemRequest> typedQuery = entityManager.createQuery(criteriaQuery);

        if (indexOfFirstElement!=null && numberOfElemenets != null) {
            typedQuery.setFirstResult(indexOfFirstElement).setMaxResults(numberOfElemenets);
        }

        List<ItemRequest> itemRequests = typedQuery.getResultList();

        return Optional.ofNullable(itemRequests);
    }

}
