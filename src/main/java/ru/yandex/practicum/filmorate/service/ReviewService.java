package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.impl.EventDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdInPathException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.eventEnum.EventOperation;
import ru.yandex.practicum.filmorate.model.eventEnum.EventType;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDao reviewDao;
    private final EventDaoImpl eventDaoImpl;

    public Review create(Review review) {
        CheckForId.idCheck(review.getFilmId());
        CheckForId.idCheck(review.getUserId());
        Review createdReview = reviewDao.save(review);
        reviewDao.addReviewRate(createdReview.getReviewId());
        eventDaoImpl.addEvent(review.getUserId(), EventType.REVIEW, EventOperation.ADD, review.getReviewId());
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
        Review newReview = reviewDao.update(review);
        Review corReview = reviewDao.findById(review.getReviewId());
        eventDaoImpl.addEvent(corReview.getUserId(), EventType.REVIEW, EventOperation.UPDATE, corReview.getReviewId());
        return newReview;
    }

    public void delete(int id) {
        CheckForId.idCheck(id);
        Review delReview = reviewDao.findById(id);
        reviewDao.delete(id);
        eventDaoImpl.addEvent(delReview.getUserId(), EventType.REVIEW, EventOperation.REMOVE, delReview.getReviewId());
    }

    public void addLikeOrDislike(int reviewId, int userId, String eventType) {
        int result = reviewDao.addLikeOrDislike(reviewId, userId, eventType);
        if (result != 1) {
            throw new InvalidIdInPathException("один из параметров неверен");
        }
        reviewDao.updateReviewRate(reviewId, userId);
    }
}
