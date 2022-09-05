package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Service
public class MpaService {

    private final MpaDaoImpl mpaDao;

    public MpaService(MpaDaoImpl mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Mpa findMpa(int mpaId) {
        return mpaDao.findMpa(mpaId);
    }

    public Collection<Mpa> findAll() {
        return mpaDao.findAll();
    }
}
