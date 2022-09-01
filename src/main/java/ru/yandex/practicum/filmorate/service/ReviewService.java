package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdInPathException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    ReviewDao reviewDao;

    @Autowired
    public ReviewService(ReviewDao dao) {
        this.reviewDao = dao;
    }

    public Review create(Review review) {
        CheckForId.idCheck(review.getFilmId());
        CheckForId.idCheck(review.getUserId());
        Review createdReview = reviewDao.save(review);
        reviewDao.addReviewRate(createdReview.getReviewId());
        return review;
    }

    public Review getById(int id) {
        CheckForId.idCheck(id);
        return reviewDao.findById(id);
    }

    public List<Review> getReviewByFilmIdAndCount(Integer id, Integer count) {
        if (id != null && count != null) {
            return reviewDao.getByFilmIdAndCount(id, count);
        } else if (id != null) {
            return reviewDao.getAllByFilmId(id);
        } else if (count != null) {
            return reviewDao.getByCount(count);
        } else return reviewDao.getAll();
    }

    public Review update(Review review) {
        CheckForId.idCheck(review.getReviewId());
        reviewDao.update(review);
        return reviewDao.findById(review.getReviewId());

    }

    public void delete(int id) {
        CheckForId.idCheck(id);
        reviewDao.delete(id);
    }

    public void addLikeOrDislike(int reviewId, int userId, String eventType) {
        int result = reviewDao.addLikeOrDislike(reviewId, userId, eventType);
        if (result != 1) {
            throw new InvalidIdInPathException("один из параметров неверен");
        }
        reviewDao.updateReviewRate(reviewId, userId);
    }
}
