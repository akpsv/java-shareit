package ru.practicum.shareit.error;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ErrorResponse {
    private String error;
    private String message;
}
