package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum SortParam {

    YEAR("year"), LIKES("likes");

    private final String param;

    SortParam(String param) {
        this.param = param;
    }
}
