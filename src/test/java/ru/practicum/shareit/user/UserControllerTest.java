package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService stubUserService;
    @InjectMocks
    UserController userController;

    ObjectMapper mapper = new ObjectMapper();

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
    }

    @Test
    void addUser_UserDto_ReturnsUserDtoWithId() throws Exception {
        //Подготовка
        UserDto userDtoIn = TestHelper.createUserDto(0L, "user1", "user1@email.ru");
        UserDto userDtoOut = TestHelper.createUserDto(1L, "user1", "user1@email.ru");
        Mockito.when(stubUserService.addUser(Mockito.any())).thenReturn(Optional.of(userDtoOut));

        //Действия
        //Проверка
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(userDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", Matchers.is(userDtoOut.getName())))
                .andExpect(jsonPath("$.email", Matchers.is(userDtoOut.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        long userID = 1L;
        UserDto userDtoOut = TestHelper.createUserDto(1L, "user1new", "user1new@email.ru");
        Mockito.when(stubUserService.updateUser(Mockito.any(), Mockito.anyLong())).thenReturn(Optional.of(userDtoOut));

        //Действия и проверка
        mvc.perform(patch("/users/{userId}", userID)
                        .content(mapper.writeValueAsString(userDtoOut))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user1new"))
                .andExpect(jsonPath("$.email").value("user1new@email.ru"));
    }

    @Test
    void getUserById() throws Exception {
        long userID = 1L;
        UserDto userDtoOut = TestHelper.createUserDto(1L, "user1", "user1@email.ru");
        Mockito.when(stubUserService.getUserById(Mockito.anyLong())).thenReturn(Optional.of(userDtoOut));

        //Действия и проверка
        mvc.perform(get("/users/{userId}", userID))
                .andExpect(jsonPath("$.id").value(userDtoOut.getId()))
                .andExpect(jsonPath("$.name").value(userDtoOut.getName()))
                .andExpect(jsonPath("$.email").value(userDtoOut.getEmail()));
    }


    @Test
    void deleteUserById() throws Exception {
        long userID = 1L;

        mvc.perform(delete("/users/{userId}", userID))
                .andExpect(status().isOk());
    }
}