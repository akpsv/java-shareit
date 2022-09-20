package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder(toBuilder = true)
public class ItemDto {
    private long id;
    @NotBlank(groups = {Create.class}, message = "Поле name не может быть пустым")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Поле description не может быть пустым")
    private String description;
    @NotNull(groups = {Create.class}, message = "Поле available не может быть null")
    private Boolean available;
}
