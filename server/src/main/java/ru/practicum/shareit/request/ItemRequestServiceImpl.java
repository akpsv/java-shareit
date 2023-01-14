package ru.practicum.shareit.request;

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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@NoArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    EntityManager entityManager;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, EntityManager entityManager) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<ItemRequestDtoOut> addItemRequest(long requestorId, ItemRequestDtoIn itemRequestDtoIn) {
        checkRequestorId(requestorId);

        itemRequestDtoIn = itemRequestDtoIn.toBuilder().requestor(requestorId).build();
        return getItemRequestDtoOut(itemRequestDtoIn);
    }

    private void checkRequestorId(long requestorId) {
        userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Запрашивающий пользователь не найден"));
    }

    private Optional<ItemRequestDtoOut> getItemRequestDtoOut(ItemRequestDtoIn itemRequestDtoIn) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoIn);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toItemRequestDtoOut(savedItemRequest);
        return Optional.ofNullable(itemRequestDtoOut);
    }

    /**
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

