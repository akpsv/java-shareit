package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    /**
     * @param requestorId
     * @param indexOfFirstElement
     * @param numberOfElemenets
     * @return
     */
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<ItemRequestDtoOut> addItemRequest(long requestorId, ItemRequestDtoIn itemRequestDtoIn) {
        checkRequestorId(requestorId);

        itemRequestDtoIn = itemRequestDtoIn.toBuilder().requestor(requestorId).build();
        return getItemRequestDtoOut(itemRequestDtoIn);
    }

    private void checkRequestorId(long requestorId) {
        userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Запрашивающий пользователь не найден"));
    }

//    private void checkItemRequestDtoIn(ItemRequestDtoIn itemRequestDtoIn) {
//        if (itemRequestDtoIn.getDescription() == null || itemRequestDtoIn.getDescription().isBlank()) {
//            throw new BadRequestException("Поле с описанием пустое.");
//        }
//    }

    private Optional<ItemRequestDtoOut> getItemRequestDtoOut(ItemRequestDtoIn itemRequestDtoIn) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoIn);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(savedItemRequest);
        return Optional.ofNullable(itemRequestDtoOut);
    }

    /**
     * TODO:
     *  Получить все запросы пользователя
     *  Преобразовать из запроса в соответствующие дто
     *  Вернуть список дто
     *
     * @param requestorId
     * @return
     */
    @Override
    public Optional<List<ItemRequestDtoOut>> getItemRequestsOfRequestor(long requestorId) {
        userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Такой пользователь не найден"));
        List<ItemRequestDtoOut> itemRequestDtoOuts = itemRequestRepository.getItemRequestsByRequestor(requestorId).get().stream().map(itemRequest -> ItemRequestMapper.toItemRequestDtoOut(itemRequest)).sorted(Comparator.comparing(ItemRequestDtoOut::getCreated).reversed()).collect(Collectors.toList());
        return Optional.ofNullable(itemRequestDtoOuts);
    }

    @Override
    public Optional<List<ItemRequestDtoOut>> getItemRequestsCreatedAnotherUsers(long requestorId, Integer indexOfFirstElement, Integer numberOfElemenets) {
        List<ItemRequestDtoOut> itemRequestDtoOuts = itemRequestRepository.getItemRequestsCreatedAnotherUsers(requestorId, entityManager, indexOfFirstElement, numberOfElemenets)
                .get().stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoOut(itemRequest))
                .collect(Collectors.toList());
        return Optional.of(itemRequestDtoOuts);
    }

    @Override
    public Optional<ItemRequestDtoOut> getItemRequestById(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Запроашивающий пользователь с таким идентификатором не найден."));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с таким идентификатором не найден"));
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(itemRequest);
        return Optional.of(itemRequestDtoOut);
    }


}

