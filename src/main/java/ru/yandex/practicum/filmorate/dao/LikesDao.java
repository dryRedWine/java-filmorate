package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface LikesDao {

    void putLike(long film_id, long favId);

    void deleteLike(long film_id, long favId);

    List<Long> findAllFilmIdFromLikes(long userId);

    List<Long> findAllUserIdFromLikes();
}
