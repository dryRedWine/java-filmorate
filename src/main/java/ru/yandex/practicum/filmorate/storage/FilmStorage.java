package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getList();

    void put(Long id, Film Film);

    Boolean contains(Film Film);

    Boolean contains(long id);

    Film getFilmById(long id);

    int getSize();
}
