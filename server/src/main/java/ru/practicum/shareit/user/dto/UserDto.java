package ru.practicum.shareit.user.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class UserDto {
    private long id;
    private String name;
    private String email;
}
