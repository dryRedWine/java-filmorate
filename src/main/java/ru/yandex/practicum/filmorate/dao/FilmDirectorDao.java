package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.Set;

public interface FilmDirectorDao {
    void addFilmDirector(Film film);

    void setFilmDirector(Film film);

    Set<Director> getFilmDirectors(Film film);

    void deleteFilmDirector(long id);

    Film updateFilmDirector(Film film);

    void saveFilmDirector(long filmId, Set<Director> directors);

}
