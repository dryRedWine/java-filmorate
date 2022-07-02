package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Film {

    @Positive
    private Integer id;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    @NotBlank(message = "Description cannot be empty or null")
    @Size(min = 2, max = 200, message = "Description must be between 1 and 200 characters")
    private String description;

    @NotNull(message = "ReleaseDate cannot be null")
    private LocalDate releaseDate;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration can be only positive")
    private Integer duration;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

}
