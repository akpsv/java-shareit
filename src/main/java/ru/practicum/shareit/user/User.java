package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Value
@Builder(toBuilder = true)
public class User {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
}
