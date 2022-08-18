package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
@Slf4j
public class GenreService {

    private final GenreDaoImpl genreDao;

    public GenreService(GenreDaoImpl genreDao) {
        this.genreDao = genreDao;
    }

    public Genre findGenre(int mpaId) {
        return genreDao.findGenre(mpaId);
    }

    public Collection<Genre> findAll() {
        return genreDao.findAll();
    }
}