package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.IllegalLoginException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectPathException;
import ru.yandex.practicum.filmorate.exceptions.NotBurnYetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public @Valid Film create(@Valid @RequestBody Film film)
            throws AlreadyExistException, NotBurnYetException, IllegalLoginException {
        log.info("Create film");
        return filmService.create(film);
    }

    @PutMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public @Valid Film update(@Valid @RequestBody Film film) {
        log.info("Update film");
        return filmService.update(film);
    }

    //  Пользователь ставит лайк фильму
    @PutMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putLikeToFilm(@PathVariable(value = "id") Long id,
                              @PathVariable(value = "userId") Long userId) {
        log.info("Put like to film");
        filmService.putLikeToFilm(id, userId);
    }

    @GetMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> get() {
        log.info("Get films");
        return filmService.get();
    }

    //  Пользователь получает фильм по id
    @GetMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable(value = "id") Long id) {
        log.info("Get film id={}", id);
        return filmService.getFilmById(id);
    }

    //  Пользователь удаляет лайк фильму
    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeToFilm(@PathVariable(value = "id") Long id,
                                 @PathVariable(value = "userId") Long userId) {
        log.info("Delete like from {} by {}", id, userId);
        filmService.deleteLikeToFilm(id, userId);
    }

    @DeleteMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilm(@PathVariable(value = "id") Long id) {
        log.info("Delete film id={}", id);
        filmService.deleteFilm(id);
    }

    @GetMapping("/films/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> searchFilms(@RequestParam(value = "query", required = false) String query,
                                        @RequestParam(value = "by", required = false) List<String> by) {
        log.info("Search films");
        if (query == null && by == null)
            return filmService.getPopularFilms(10L);
        else if (query == null || by == null)
            throw new IncorrectPathException("Передан неправильный параметр запроса");
        return filmService.searchFilms(query, by);
    }


    @GetMapping("/films/director/{directorId}")
    public List<Film> sortByFilm(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("Get sort by film");
        return filmService.findAllFilmsOfDirectorSorted(directorId, sortBy);
    }


    @GetMapping("/films/common")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getCommonFilms(@RequestParam(value = "userId") Long userId,
                                           @RequestParam(value = "friendId") Long friendId) {
        log.info("Get common films");
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/films/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilmsOrderByGenreYear
            (@RequestParam(value = "count", defaultValue = "10", required = false) long count,
             @RequestParam Optional<Long> genreId,
             @RequestParam Optional<Integer> year) {
        log.info("Get popular films");
        return filmService.getPopularFilmsOrderByGenreYear(genreId, year, count);
    }
}
