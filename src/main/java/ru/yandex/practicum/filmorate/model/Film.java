package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

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
    private Mpa mpa;

    private LinkedHashSet<Genre> genres;

    private Set<Director> directors = new HashSet<>();

    public void setDirectors(Set<Director> directors) {
        this.directors = directors;
    }

    public Set<Director> getDirectors() {
        return directors;
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

    public void setId(Long id) {
        this.id = id;
    }
}
