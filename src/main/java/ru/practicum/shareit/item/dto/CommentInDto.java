package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder(toBuilder = true)
public class CommentInDto {
    private long id;
    @NotBlank
    private String text;
}
