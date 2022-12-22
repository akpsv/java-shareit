package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<List<Item>> findByOwnerIdEquals(long ownerId);

    Optional<List<Item>> findByNameOrDescriptionContainingIgnoreCaseAndAvailable(String searchStringIntoName, String searchStringIntoDesctiption, boolean isAvailable);
}
