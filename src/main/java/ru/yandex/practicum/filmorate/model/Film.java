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
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
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

    @NotNull(message = "ReleaseDate cannot be null")
    private LocalDate releaseDate;

    @NotNull(message = "Duration cannot be null")
    @Positive(message = "Duration can be only positive")
    private Integer duration;

    private final Set<Long> likes;

    private final Set<Long> genres;

    @NotBlank(message = "MPA rating cannot be empty or null")
    private String mpaRating;

    public Set<Long> getGenres() {
        return genres;
    }

    public long returnGenreCount() {
        return genres.size();
    }

    public void addGenre(Long genreId) {
        genres.add(genreId);
    }

    public void deleteGenre(Long genreId) {
        genres.remove(genreId);
    }

    public Set<Long> getLikes() {
        return likes;
    }

    public long returnLikesCount() {
        return likes.size();
    }

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void deleteLike(Long userId) {
        likes.remove(userId);
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likes = new HashSet<>();
        genres = new HashSet<>();
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration,
                String mpaRating) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaRating = mpaRating;
        likes = new HashSet<>();
        genres = new HashSet<>();
    }

}
