package ru.practicum.shareit.request.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoIn {
    private long id;
    private String description;
    private long requestor; //Ид пользователя, который создал запрос
    private Date created;
}
