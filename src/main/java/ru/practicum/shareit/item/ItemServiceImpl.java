package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<Item> add(ItemDto itemDto, long ownerId) {
        return itemMapper.toItem(itemDto)
                .map(item -> item.toBuilder().ownerId(ownerId).build())
                .map(item -> itemRepository.save(item));
    }

    /**
     * Обновление вещи информацией из dto если вещь принадлежит пользователю с соответствующим ид
     *
     * @param itemDto - объект содержит данные вещи
     * @param itemId  - ид вещи
     * @param userId  - ид владельца вещи
     * @return - вещь или ничего
     */
    @Override
    public Optional<ItemDto> update(final ItemDto itemDto, final long itemId, final long userId) {
        itemRepository.findById(itemId)
                .filter(item -> item.getOwnerId() == userId)
                .orElseThrow(() -> new NotFoundException("Ид владельца не правильный"));

        return Optional.of(itemDto)
                .flatMap(dto -> itemRepository.findById(itemId)
                        .map(item -> updateItemFromDto(dto, item))
                        .flatMap(updatingItem -> Optional.of(itemRepository.save(updatingItem))))
                .flatMap(item -> itemMapper.toDto(item));
    }

    private Item updateItemFromDto(ItemDto dto, Item item) {
        Item updatingItem = item;
        if (dto.getName() != null) {
            updatingItem = updatingItem.toBuilder().name(dto.getName()).build();
        }
        if (dto.getDescription() != null) {
            updatingItem = updatingItem.toBuilder().description(dto.getDescription()).build();
        }
        if (dto.getAvailable() != null) {
            updatingItem = updatingItem.toBuilder().available(dto.getAvailable()).build();
        }
        return updatingItem;
    }

    @Override
    public Optional<Item> getItemById(final long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public Optional<List<Item>> getOwnerItems(final long userId) {
        return itemRepository.findByOwnerIdEquals(userId);
    }

    @Override
    public Optional<List<Item>> searchItems(String searchingText) {
        if (searchingText.isBlank()) {
            return Optional.of(Collections.emptyList());
        }
        Optional<List<Item>> byNameAndDescriptionContainingIgnoreCase = itemRepository.findByNameOrDescriptionContainingIgnoreCaseAndAvailable(searchingText, searchingText, true);
        return byNameAndDescriptionContainingIgnoreCase;
    }

    @Transactional
    @Override
    public Comment addComment(long bookerId, long itemId, String comment) {
        Optional<List<Booking>> bookingsByBookerId = bookingRepository.findByBookerIdEquals(bookerId);
        if (!bookingsByBookerId.isPresent()) {
            throw new EntityNotFoundException("Нельзя добавить комментарий. Пользователь не брал вещь в аренду");
        }
        Item item = bookingsByBookerId.get().stream()
                .filter(booking ->
                        booking.getItem().getId() == itemId &&
                                booking.getStatus().equals(BookingStatus.APPROVED) &&
                                booking.getStart().isBefore(LocalDateTime.now())
                )
                .map(booking -> booking.getItem())
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Нельзя добавить комментарий. Пользователь не брал вещь в аренду"));

        Comment addingComment = commentRepository.save(Comment.builder()
                .item(item)
                .author(userRepository.findById(bookerId).get())
                .text(comment)
                .build()
        );

        item.getComments().add(addingComment);
        itemRepository.save(item);

        return addingComment;
    }
}
