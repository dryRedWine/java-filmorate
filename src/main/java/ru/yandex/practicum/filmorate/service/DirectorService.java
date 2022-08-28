package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.InvalidIdInPathException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Director get(long id) {
        final Director directorById = directorDao.get(id);
        if (directorById == null) {
            throw new InvalidIdInPathException("Director with id=" + id + "not found");
        }
        return directorById;
    }

    public List<Director> getAll() {
        return directorDao.getAll();
    }

    public Director create(Director director) {
        final Director directorFromStorage = directorDao.get(director.getId());
        if (directorFromStorage == null) {
            directorDao.create(director);
        } else throw new AlreadyExistException(String.format(
                "Режиссер с таким id %s уже зарегистрирован.", director.getId()));
        return director;
    }

    public Director update(Director director) {
        final Director directorFromStorage = directorDao.get(director.getId());
        if (directorFromStorage == null) {
            throw new InvalidIdInPathException("Director with id=" + director.getId() + "not found");
        }
        directorDao.update(director);
        return director;
    }

    public void delete(long id) {
        final Director directorFromStorage = directorDao.get(id);
        if (directorFromStorage == null) {
            throw new InvalidIdInPathException("Director with id=" + id + "not found");
        }
        directorDao.delete(directorFromStorage);
    }


}
