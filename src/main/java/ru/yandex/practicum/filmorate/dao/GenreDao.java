package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;

public interface GenreDao {

    Genre findGenre(long genreId);

    LinkedHashSet<Genre> findAll();

    void saveGenres(long filmId, LinkedHashSet<Genre> genres);

    void deleteGenres(long filmId);

    LinkedHashSet<Genre> getGenresById(long film_id);
}
