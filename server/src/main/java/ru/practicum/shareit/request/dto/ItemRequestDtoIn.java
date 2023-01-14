package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoIn {
    private long id;
    @NotNull(groups = {Create.class}, message = "Поле описания не может быть null")
    @NotBlank(groups = {Create.class}, message = "Поле описания не может быть пустым")
    private String description;
    private long requestor; //Ид пользователя, который создал запрос
    private Date created;
}
