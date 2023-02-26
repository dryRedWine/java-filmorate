package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdInPathException;
import ru.yandex.practicum.filmorate.exceptions.NegativeIdException;
import ru.yandex.practicum.filmorate.exceptions.NotBurnYetException;

import java.util.Map;

@RestControllerAdvice(value = {
        "ru.yandex.practicum.filmorate.controllers",
        "ru.yandex.practicum.filmorate.service",
        "ru.yandex.practicum.filmorate.storage"})
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNegativeIdException(final NegativeIdException e) {
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleInvalidIdInPathException(final InvalidIdInPathException e) {
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAlreadyExistException(final AlreadyExistException e) {
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleINotBurnYetException(final NotBurnYetException e) {
        return Map.of("Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(final IllegalArgumentException e) {
        return Map.of("Error", e.getMessage());
    }
}
