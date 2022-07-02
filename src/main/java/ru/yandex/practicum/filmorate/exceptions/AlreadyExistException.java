package ru.yandex.practicum.filmorate.exceptions;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String message) {
        super(message);
    }
}
