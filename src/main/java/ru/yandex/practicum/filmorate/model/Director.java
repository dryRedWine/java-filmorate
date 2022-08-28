package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
public class Director {
    private long id;

    @NotBlank(message = "Name cannot be empty or null")
    String name;
}
