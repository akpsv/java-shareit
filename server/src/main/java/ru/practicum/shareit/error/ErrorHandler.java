package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice(assignableTypes = {ItemController.class, UserController.class, BookingController.class, ItemRequestController.class})
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse someExceptionHandler(NotFoundException e) {
        return ErrorResponse.builder()
                .error("Элемент не найден")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse enitityNotFound(EntityNotFoundException e) {
        return ErrorResponse.builder()
                .error("Сущность не найдена")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationExceptionHandler(MethodArgumentNotValidException e) {
        return ErrorResponse.builder()
                .error("Ошибка при проверке аргумента метода на соответствие ограничениям.")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ItemNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse itemNotAbailableExceptionHandler(ItemNotAvailableException e) {
        return ErrorResponse.builder()
                .error("Вещь не доступна для бронирования")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationExceptionHandler(ConstraintViolationException e) {
        return ErrorResponse.builder()
                .error("Нарушено ограничение поля")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(EnumException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse enumExceptionHandler(EnumException e) {
        return ErrorResponse.builder()
                .error("Unknown state: UNSUPPORTED_STATUS")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestExceptionHandler(BadRequestException e) {
        return ErrorResponse.builder()
                .error("Не правильный запрос")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse otherExceptionHandler(Throwable e) {
        return ErrorResponse.builder()
                .error("Ошибка на сервере")
                .message(e.getMessage())
                .build();
    }
}
