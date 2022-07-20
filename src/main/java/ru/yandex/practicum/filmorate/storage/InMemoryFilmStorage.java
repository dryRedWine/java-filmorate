package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new LinkedHashMap<>();

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void putFilm(Long id, Film film) {
        films.put(id, film);
    }

    @Override
    public Boolean containsFilm(Film film) {
        return films.containsValue(film);
    }

    @Override
    public Boolean containsFilm(long id) {
        return films.containsKey(id);
    }

    @Override
    public Film returnFilmById(long id) {
        return films.get(id);
    }

    @Override
    public int getSize() {
        return films.size();
    }
}
