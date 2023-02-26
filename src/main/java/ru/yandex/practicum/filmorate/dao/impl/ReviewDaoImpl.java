package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdInPathException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.utility.EventType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class ReviewDaoImpl implements ReviewDao {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review save(Review review) {
        String sqlQuery = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, review.getContent());
                stmt.setBoolean(2, review.getIsPositive());
                stmt.setInt(3, review.getUserId());
                stmt.setInt(4, review.getFilmId());
                return stmt;
            }, keyHolder);
        } catch (RuntimeException ex) {
            throw new InvalidIdInPathException("передан неверный id");
        }
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Ревью  успешно сохранено в таблице reviews");
        return findById(review.getReviewId());
    }

    @Override
    public Review findById(int id) {
        final String sqlQuery = "SELECT r.*, " +
                "rr.rate useful " +
                "FROM reviews r " +
                "LEFT JOIN review_rate rr ON r.id = rr.review_id " +
                "WHERE r.id = ? ";

        final List<Review> reviews = jdbcTemplate.query(sqlQuery, this::makeReview, id);
        if (reviews.size() != 1) {
            throw new InvalidIdInPathException("ревью с таким id не найден");
        }
        return reviews.get(0);

    }

    @Override
    public Review update(Review review) {
        String sqlQuery =
                "UPDATE reviews SET content = ?,is_positive = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE ID = ?";
        Object[] args = new Object[]{id};
        jdbcTemplate.update(sql, args);
    }

    @Override
    public List<Review> getAll() {
        final String sqlQuery = "SELECT r.*, " +
                "rr.rate AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_rate rr ON r.id = rr.review_id " +
                "GROUP BY r.id, useful " +
                "ORDER BY useful DESC";
        return jdbcTemplate.query(sqlQuery, this::makeReview);
    }

    @Override
    public List<Review> getAllByFilmId(int filmId) {
        final String sqlQuery = "SELECT r.*, " +
                "rr.rate AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_rate rr ON r.id = rr.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.id, useful " +
                "ORDER BY useful DESC";

        return jdbcTemplate.query(sqlQuery, this::makeReview, filmId);
    }

    @Override
    public List<Review> getByCount(int count) {
        final String sqlQuery = "SELECT r.*, " +
                "rr.rate AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_rate rr ON r.id = rr.review_id " +
                "GROUP BY r.id, useful " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeReview, count);
    }

    @Override
    public List<Review> getByFilmIdAndCount(int filmId, int count) {
        final String sqlQuery = "SELECT r.*, " +
                "rr.rate AS useful " +
                "FROM reviews r " +
                "LEFT JOIN review_rate rr ON r.id = rr.review_id " +
                "WHERE r.film_id = ? " +
                "GROUP BY r.id, useful " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeReview, filmId, count);
    }

    @Override
    public int addLikeOrDislike(int reviewId, int userId, String eventType) {
        String sql = "INSERT INTO review_events (user_id, review_id, event_type) VALUES ( ?, ?, ?)";
        return jdbcTemplate.update(sql, userId, reviewId, eventType);
    }

    @Override
    public void updateReviewRate(int reviewId, int userId) {
        String sqlQuery =
                "UPDATE review_rate " +
                        "SET rate = " +
                        "(SELECT COUNT(user_id) FROM review_events WHERE event_type = ? AND review_id =?) " +
                        "- (SELECT COUNT(user_id) FROM review_events WHERE event_type = ? AND review_id =?) " +
                        "WHERE review_id = ? ";
        jdbcTemplate.update(sqlQuery,
                EventType.LIKE.toString(),
                reviewId,
                EventType.DISLIKE.toString(),
                reviewId,
                reviewId);
    }

    @Override
    public void addReviewRate(int id) {
        String sql = "INSERT INTO review_rate (review_id, rate) VALUES ( ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setInt(1, id);
            stmt.setInt(2, 0);

            return stmt;
        }, keyHolder);
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .content(rs.getString("content"))
                .reviewId(rs.getInt("id"))
                .useful(rs.getInt("useful"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id")).build();
    }
}
