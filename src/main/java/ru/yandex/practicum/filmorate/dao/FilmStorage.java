package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Film saveFilm(Film Film);

    Boolean contains(Film Film);

    Boolean contains(long id);

    Film getFilmById(long id);

    int getSize();

    Film update(Film film);

    List<Film> getPopularFilms(long film_id);

    List<Film> getPopularFilmsOrderByGenreYear(Optional<Long> genreId, Optional<Integer> year, long count);
}
