package ru.yandex.practicum.filmorate.exceptions;

public class IncorrectParameterException extends RuntimeException {
    public IncorrectParameterException(String message) {
        super(message);
    }
}
