package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 * Основная сущность сервиса, вокруг которой будет строиться вся дальнейшая работа, — вещь.
 * В коде она будет фигурировать как Item.
 * Пользователь, который добавляет в приложение новую вещь, будет считаться ее владельцем.
 * При добавлении вещи должна быть возможность указать её краткое название и добавить небольшое описание.
 * К примеру, название может быть — «Дрель “Салют”»,
 * а описание — «Мощность 600 вт, работает ударный режим, так что бетон возьмёт».
 * Также у вещи обязательно должен быть статус — доступна ли она для аренды.
 * Статус должен проставлять владелец.
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
