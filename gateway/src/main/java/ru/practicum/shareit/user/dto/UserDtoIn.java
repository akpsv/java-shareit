package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class UserDtoIn {
    private long id;
    @NotBlank(message = "Email не может быть null")
    @Email(message = "В поле email должен быть адрес электронной почты")
    private String email;
    private String name;
}
