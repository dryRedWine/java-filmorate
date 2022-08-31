package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.IllegalLoginException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.NotBurnYetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping
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
        return filmService.create(film);
    }

    @PutMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public @Valid Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    //  Пользователь ставит лайк фильму
    @PutMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putLikeToFilm(@PathVariable(value = "id") Long id,
                              @PathVariable(value = "userId") Long userId) {
        filmService.putLikeToFilm(id, userId);
    }

    @GetMapping("/films")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> get() {
        return filmService.get();
    }

    //  Пользователь получает фильм по id
    @GetMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable(value = "id") Long id) {
        return filmService.getFilmById(id);
    }

    //  Возвращает список из первых count фильмов по количеству лайков.
 /*   //  Если значение параметра count не задано, верните первые 10
    @GetMapping("/films/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        if (count < 1)
            throw new IncorrectParameterException("Count не может быть меньше 1");
        return filmService.getPopularFilms(count);
    }
*/
    //  Пользователь удаляет лайк фильму
    @DeleteMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeToFilm(@PathVariable(value = "id") Long id,
                                 @PathVariable(value = "userId") Long userId) {
        filmService.deleteLikeToFilm(id, userId);
    }

    @GetMapping("/films/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopularFilmsOrderByGenreYear
            (@RequestParam(value = "count", defaultValue = "10", required = false) long count,
             @RequestParam(value = "genreId" ) Optional<Long> genreId,
             @RequestParam(value = "year" ) Optional<Integer> year) {
        return filmService.getPopularFilmsOrderByGenreYear( genreId, year, count);
    }
}
