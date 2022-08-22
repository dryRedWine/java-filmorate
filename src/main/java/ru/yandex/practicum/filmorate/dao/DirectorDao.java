package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDao {
    Director get(long id);
    List<Director> getAll();
    Director create(Director director);
    Director update(Director director);
    void delete(Director director);

    boolean contains(long id);

}
