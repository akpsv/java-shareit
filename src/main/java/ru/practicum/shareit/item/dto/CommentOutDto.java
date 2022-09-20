package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder(toBuilder = true)
public class CommentOutDto {
    private long id;
    private String text;
    private String authorName;
    private Date created;
}
