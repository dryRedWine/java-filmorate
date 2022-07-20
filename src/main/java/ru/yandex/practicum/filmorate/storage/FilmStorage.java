package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilmsList();

    void putFilm(Long id, Film Film);

    Boolean containsFilm(Film Film);

    Boolean containsFilm(long id);

    Film returnFilmById(long id);

    int getSize();
}
