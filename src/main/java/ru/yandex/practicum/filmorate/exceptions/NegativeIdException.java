package ru.yandex.practicum.filmorate.exceptions;

public class NegativeIdException extends RuntimeException {
    public NegativeIdException(String message) {
        super(message);
    }
}
