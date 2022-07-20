package ru.yandex.practicum.filmorate.exceptions;

public class InvalidIdInPathException extends RuntimeException {
    public InvalidIdInPathException(String message) {
        super(message);
    }
}
