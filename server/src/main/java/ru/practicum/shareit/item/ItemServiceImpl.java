package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private CommentRepository commentRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private BookingService bookingService;
    private ItemRequestRepository itemRequestRepository;
    private EntityManager entityManager;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository, BookingRepository bookingRepository, UserRepository userRepository, BookingService bookingService, ItemRequestRepository itemRequestRepository, EntityManager entityManager) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.itemRequestRepository = itemRequestRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<ItemDto> add(ItemDto itemDto, long ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Ид пользователя не найдено"));

        Optional<Item> resultItem = ItemMapper.toItem(itemDto, itemRequestRepository)
                .map(item -> item.toBuilder().ownerId(ownerId).build())
                .map(item -> itemRepository.save(item));
        Optional<ItemDto> itemDto1 = ItemMapper.toDto(resultItem.get());
        return itemDto1;
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

        Optional<ItemDto> itemDto1 = Optional.of(itemDto)
                .flatMap(dto -> itemRepository.findById(itemId)
                        .map(item -> updateItemFromDto(dto, item))
                        .flatMap(updatingItem -> Optional.of(itemRepository.save(updatingItem))))
                .flatMap(item -> ItemMapper.toDto(item));
        return itemDto1;
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
    public Optional<ItemOutDto> getItemOutDtoById(final long itemId, final long userId) {
        //Проверить существует ли вещь по ид
        Optional<Item> itemById = getItemById(itemId);
        if (!itemById.isPresent()) {
            throw new EntityNotFoundException("Такая вещь не найдена");
        }
        //Преобразовать сущность в дто
        Optional<ItemOutDto> itemOutDto = ItemMapper.toOutDto(itemById.get(), bookingService);
        if (!itemById.filter(item -> item.getOwnerId() == userId).isPresent()) {
            return itemOutDto.map(dto -> dto.toBuilder().lastBooking(null).nextBooking(null).build());
        }
        return itemOutDto;
    }

    @Override
    public Optional<List<Item>> getOwnerItems(final long userId, Integer from, Integer size) {
        return itemRepository.getOwnerItems(entityManager, userId, from, size);
    }

    @Override
    public Optional<List<Item>> searchItems(String searchingText, Integer from, Integer size) {
        if (searchingText.isBlank()) {
            return Optional.of(Collections.emptyList());
        }
        return itemRepository.searchItems(entityManager, searchingText, from, size);
    }

    @Transactional
    @Override
    public Optional<CommentOutDto> addComment(long bookerId, long itemId, String comment) {
        if (comment.isBlank()) {
            throw new BadRequestException("Пустое поле comment");
        }

        Optional<List<Booking>> bookingsByBookerId = bookingRepository.findByBookerIdEquals(bookerId);
        if (!bookingsByBookerId.isPresent()) {
            throw new EntityNotFoundException("Нельзя добавить комментарий. Пользователь не брал вещь в аренду");
        }
        Item item = bookingsByBookerId.get().stream()
                .filter(booking ->
                                booking.getItem().getId() == itemId &&
                                        booking.getStatus().equals(BookingStatus.APPROVED) &&
//                                booking.getStart().isBefore(LocalDateTime.now())
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

        return Optional.ofNullable(CommentMapper.toOutDto(addingComment));
    }
}
