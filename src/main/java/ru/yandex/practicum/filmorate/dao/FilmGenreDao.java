package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;

public interface FilmGenreDao {

    void saveFilmGenre(long filmId, LinkedHashSet<Genre> genres);

    void deleteFilmGenre(long filmId);

    LinkedHashSet<Genre> getFilmGenreById(long film_id);
}
