package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum SearchName {
    TITLE("title"), DIRECTOR("director");

    private final String name;

    SearchName(String name) {
        this.name = name;
    }
}
