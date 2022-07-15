package ru.yandex.practicum.filmorate.exceptions;

public class IllegalLoginException extends RuntimeException {
    public IllegalLoginException(String message) {
        super(message);
    }
}
