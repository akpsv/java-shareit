package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.TimeFieldMatcher;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@TimeFieldMatcher(fieldNameOfStartTime = "start", fieldNameOfEndTime = "end", message = "Дата в поле start не может быть ранее даты в поле end")
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
