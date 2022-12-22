package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentOutDto toOutDto(Comment comment) {
        return CommentOutDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}
