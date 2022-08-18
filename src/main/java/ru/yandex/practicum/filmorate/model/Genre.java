package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Positive;

@Builder
@EqualsAndHashCode
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Genre {

    @Positive
    private Integer id;

    private String name;
}
