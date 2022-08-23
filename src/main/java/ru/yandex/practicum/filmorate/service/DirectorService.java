package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.utility.CheckForId;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Director get(long id) {
        return directorDao.get(id);
    }

    public List<Director> getAll() {
        return directorDao.getAll();
    }

    public Director create(Director director) {
        if (!directorDao.contains(director.getId())) {
            return directorDao.create(director);
        } else {
            throw new  AlreadyExistException("Такой режисер уже есть в базе данных");
        }
    }

    public Director update(Director director) {
        CheckForId.idCheck(director.getId());
        return directorDao.update(director);
    }

    public void delete(Director director) {
        directorDao.delete(director);
    }


}
