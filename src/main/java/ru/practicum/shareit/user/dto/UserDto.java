package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Builder(toBuilder = true)
public class UserDto {
    private long id;
    @NotBlank(groups = {Create.class}, message = "Имя не может быть null")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Email не может быть null")
    @Email(groups = {Create.class}, message = "В поле email должен быть адрес электронной почты")
    private String email;
}
