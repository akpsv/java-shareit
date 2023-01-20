package ru.practicum.shareit.user.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class UserDtoIn {
    private long id;
    //    @NotBlank(groups = {Create.class}, message = "Имя не может быть null")
    private String name;
    //    @NotBlank(groups = {Create.class}, message = "Email не может быть null")
//    @Email(groups = {Create.class}, message = "В поле email должен быть адрес электронной почты")
    private String email;
}
