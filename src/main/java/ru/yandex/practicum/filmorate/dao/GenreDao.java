package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;

public interface GenreDao {

    Genre findGenre(long genreId);

    LinkedHashSet<Genre> findAll();

}
