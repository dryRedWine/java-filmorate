package ru.yandex.practicum.filmorate.dao;

public interface LikesDao {

    void putLike(long film_id, long favId);

    void deleteLike(long film_id, long favId);
}
