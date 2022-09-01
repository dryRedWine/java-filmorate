package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.LikesDao;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LikesDaoImpl implements LikesDao {

    private final JdbcTemplate jdbcTemplate;

    public LikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void putLike(long film_id, long favId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, film_id, favId);
    }

    @Override
    public void deleteLike(long film_id, long favId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, film_id, favId);
    }

    // Получение всех film_id таблицы лайков конкретного пользователя
    @Override
    public List<Long> findAllFilmIdFromLikes(long userId) {
        String sql = "SELECT FILM_ID FROM likes WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::makeFilmId, userId);
    }

    // Получение всех id пользователей в талице лайков
    @Override
    public List<Long> findAllUserIdFromLikes() {
        String sql = "SELECT USER_ID FROM likes";
        return jdbcTemplate.query(sql, this::makeUserId);
    }

    private Long makeUserId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("user_id");
    }

    private Long makeFilmId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("film_id");

    }

    @Override
    public Boolean contains(long filmId, long userId) {
        return jdbcTemplate.query("SELECT * FROM LIKES WHERE FILM_ID = ? and USER_ID = ?",
                ResultSet::next, filmId, userId);

    }
}
