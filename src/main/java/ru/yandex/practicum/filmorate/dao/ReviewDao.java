package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {
    Review save(Review review);

    Review findById(int id);

    Review update(Review review);

    void delete(int id);

    List<Review> getAll();

    List<Review> getAllByFilmId(int filmId);

    List<Review> getByCount(int count);

    List<Review> getByFilmIdAndCount(int filmId, int count);

    int addLikeOrDislike(int reviewId, int userId, String eventType);

    void updateReviewRate(int reviewId, int userId);

    void addReviewRate(int reviewId);

}
