package ru.practicum.shareit.item;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager entityManager;


    @Test
    void save_ItemWithoutId_ReturnsItemWithId(){
        //Подготовка
        Item newItem1 = TestHelper.createItem(0L, 1L, true, null, null);
        Item expectedItem = TestHelper.createItem(1L, 1L, true, null, null);;

        //Действия
        Item actualItem = itemRepository.save(newItem1);

        //Проверка
        assertThat(actualItem.getId(), not(0));
    }

    @Test
    void searchItems() {
    }

    @Test
    void getOwnerItems_WhenCalled_ReturnsItemsOfOwner() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user1", "user1@email.ru");
        userRepository.save(user1);
        Item item1 = TestHelper.createItem(1L, 1L, true, null, new HashSet<>());
        Item item2 = TestHelper.createItem(2L, 1L, true, null, new HashSet<>());
        itemRepository.save(item1);
        itemRepository.save(item2);

        int expectedSizeOfItemsGtoup = 2;
        //Действия
        List<Item> items = itemRepository.getOwnerItems(entityManager, 1L,0, 10).get();
        int actualSizeOfItemsGroup =items.size();

        //Проверка
        assertThat(actualSizeOfItemsGroup, equalTo(expectedSizeOfItemsGtoup));

    }
}
