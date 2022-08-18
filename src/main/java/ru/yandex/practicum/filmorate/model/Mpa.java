package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.Positive;

@Builder
@EqualsAndHashCode
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mpa {

    @Positive
    private Integer id;

    private String name;

}
