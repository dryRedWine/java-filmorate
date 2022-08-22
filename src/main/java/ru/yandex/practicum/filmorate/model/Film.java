package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@EqualsAndHashCode
@Builder
public class Film {

    // Видимо из-за того, что мы еще не проходили данную аннотацию, в тестах возвращается код 500,
    // а в случае с данной аннотацией должен возвращаться 400.
    // Но, к счастью, в других тестах есть и 400, и 500))))
//    @Positive(message = "id can be only positive")
    private Long id;

    @NotBlank(message = "Name cannot be empty or null")
    private String name;

    @NotBlank(message = "Description cannot be empty or null")
    @Size(min = 2, max = 200, message = "Description must be between 1 and 200 characters")
    private String description;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration can be only positive")
    private Integer duration;

    @NotNull(message = "ReleaseDate cannot be null")
    private LocalDate releaseDate;



//    @Positive(message = "Duration can be only positive")
//    @Max(value = 10)
//    private Integer rate;

    private final Mpa mpa;

    private LinkedHashSet<Genre> genres;

    private Set<Director> directors;

    public void setDirectors(Set<Director> directors) {
        this.directors = directors;
    }

    public LinkedHashSet<Genre> getGenres() {
        return genres;
    }

    public void setGenres(LinkedHashSet<Genre> genres) {
        this.genres = genres;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("duration", duration);
        values.put("releaseDate", releaseDate);
        values.put("mpa_id", mpa.getId());
        return values;
    }
//
//    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
//        this.name = name;
//        this.description = description;
//        this.releaseDate = releaseDate;
//        this.duration = duration;
//    }

//    public Film(String name, String description, LocalDate releaseDate, Integer duration,
//                Integer mpaRating) {
//        this.name = name;
//        this.description = description;
//        this.releaseDate = releaseDate;
//        this.duration = duration;
//        this.mpaRating = mpaRating;
//    }


}
