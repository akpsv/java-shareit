package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentOutDto {
    private long id;
    private String text;
    private String authorName;
    private Date created;
}
