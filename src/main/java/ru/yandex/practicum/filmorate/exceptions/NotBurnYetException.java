package ru.yandex.practicum.filmorate.exceptions;

public class NotBurnYetException extends RuntimeException {
    public NotBurnYetException(String message) {
        super(message);
    }
}
