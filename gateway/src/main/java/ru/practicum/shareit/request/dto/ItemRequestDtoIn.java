package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoIn {
    private long id;
    @NotBlank(message = "Поле описания не может быть пустым")
    private String description;
    private long requestor; //Ид пользователя, который создал запрос
    private Date created;
}
