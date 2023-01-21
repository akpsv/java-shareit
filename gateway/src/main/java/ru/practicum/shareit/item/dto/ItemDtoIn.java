package ru.practicum.shareit.item.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class ItemDtoIn {
    private long id;
    //    @NotBlank(groups = {Create.class}, message = "Поле name не может быть пустым")
    private String name;
    //    @NotBlank(groups = {Create.class}, message = "Поле description не может быть пустым")
    private String description;
    //    @NotNull(groups = {Create.class}, message = "Поле available не может быть null")
    private Boolean available;
    private long requestId;
}
