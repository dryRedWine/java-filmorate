package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.utility.EventType;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review review)
            throws IllegalArgumentException {
        return reviewService.create(review);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review getById(@PathVariable int id) {
        log.info("Get review id={}", id);
        return reviewService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> getByFilmIdAndCount(@RequestParam(required = false) Integer count,
                                                  @RequestParam(required = false) Integer filmId) {
        return reviewService.getReviewByFilmIdAndCount(filmId, count);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeToFilm(@PathVariable int id) {
        reviewService.delete(id);
    }

    @PutMapping("{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.addLikeOrDislike(reviewId, userId, EventType.LIKE.toString());
    }

    @PutMapping("{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislike(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.addLikeOrDislike(reviewId, userId, EventType.DISLIKE.toString());
    }
}
