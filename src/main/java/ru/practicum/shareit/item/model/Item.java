package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Value
@Builder(toBuilder = true)
public class Item {
    private int id;
    @NotBlank(groups = {Create.class}, message = "Имя не может быть пустым")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Описание не может быть пустым")
    private String description;
    @NotBlank(groups = {Create.class}, message = "Идентификатор владельца не может быть пустым")
    private int ownerId;
    @NotNull(groups = {Create.class}, message = "Статус не может быть пустым")
    private Boolean available;
    private ItemRequest request;
}
