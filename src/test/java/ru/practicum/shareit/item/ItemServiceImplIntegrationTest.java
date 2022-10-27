package ru.practicum.shareit.item;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;

//@SpringBootTest
class ItemServiceImplIntegrationTest {
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    ItemRepository itemRepository;
//    @Autowired
//    CommentRepository commentRepository;
//    @Autowired
//    ItemService itemService;

    UserRepository stubUserRepository;
    ItemRepository stubItemRepository;
    ItemRequestRepository stubItemRequestRepository;

    @BeforeEach
    void setUp() {
        stubUserRepository = Mockito.mock(UserRepository.class);
        stubItemRepository = Mockito.mock(ItemRepository.class);
        stubItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    }

    @Test
    void add_ItemDtoWithoutId_ReturnsItemDtoWithId() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        ItemRequest itemRequest1 = TestHelper.createItemRequest(1L, 1L, new HashSet<>());
        Mockito.when(stubItemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(itemRequest1));

        Item item1 = TestHelper.createItem(1L, 1L, true, null, new HashSet<>());
        Mockito.when(stubItemRepository.save(Mockito.any(Item.class))).thenReturn(item1);

        ItemService itemService = ItemServiceImpl.builder()
                .itemRepository(stubItemRepository)
                .userRepository(stubUserRepository)
                .itemRequestRepository(stubItemRequestRepository)
                .build();

        ItemDto newItemDto = TestHelper.createItemDto(0L, "item1", "description", true, 0);
        ItemDto expectedItemDto = TestHelper.createItemDto(1L, "item1", "description", true, 0);
        //Действия
        ItemDto actualItemDto = itemService.add(newItemDto, user1.getId()).get();

        //Проверка
        assertThat(actualItemDto.getId(), Matchers.equalTo(expectedItemDto.getId()));
        assertThat(actualItemDto.getName(), Matchers.equalTo(expectedItemDto.getName()));
        assertThat(actualItemDto.getDescription(), Matchers.equalTo(expectedItemDto.getDescription()));
    }

    @Test
    void update_ItemDto_ReturnsItemDtoWithNewData() {
        //Подготовка
        Item item1 = TestHelper.createItem(1L, 1L, true, null, new HashSet<>());
        Mockito.when(stubItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(item1));
        Mockito.when(stubItemRepository.save(Mockito.any(Item.class))).thenReturn(item1);

        ItemService itemService = ItemServiceImpl.builder()
                .itemRepository(stubItemRepository)
                .build();

        ItemDto newItemDto = TestHelper.createItemDto(item1.getId(), "item1new", "new description", true, 0);
        ItemDto expectedItemDto = newItemDto;

        //Действия
        ItemDto actualItemDto = itemService.update(newItemDto, newItemDto.getId(), 1L).get();
        //Проверка
        assertThat(actualItemDto.getId(), Matchers.equalTo(expectedItemDto.getId()));
        assertThat(actualItemDto.getName(), Matchers.equalTo(expectedItemDto.getName()));
        assertThat(actualItemDto.getDescription(), Matchers.equalTo(expectedItemDto.getDescription()));

    }

//    @Test
//    void getItemById_ItemId_ReturnsItem() {
//        //Подготовка
//        userRepository.deleteAll();
//        itemRepository.deleteAll();
//
//        User savedUser1 = userRepository.save(TestHelper.createUser(0L, "user1", "user1@email.ru"));
//        User savedUser2 = userRepository.save(TestHelper.createUser(0L, "user2", "user2@email.ru"));
//
//        Item savedItem1 = itemRepository.save(TestHelper.createItem(0L, savedUser1.getId(), true, null, new HashSet<>()));
//        Item savedItem2 = itemRepository.save(TestHelper.createItem(0L, savedUser2.getId(), true, null, new HashSet<>()));
//
//        Item expecetdItem = savedItem1;
//
//        //Действия
//        Item actualItem = itemService.getItemById(savedItem1.getId()).get();
//        //Проверка
//        assertThat(actualItem, samePropertyValuesAs(expecetdItem));
//
//    }

    @Test
    void getItemOutDtoById() {
    }

    @Test
    void getOwnerItems() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void addComment() {
    }
}
