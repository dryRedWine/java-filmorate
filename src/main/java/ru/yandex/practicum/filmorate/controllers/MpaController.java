package ru.yandex.practicum.filmorate.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa findMpaByID(@PathVariable(value = "id") int mpaId) {
        return mpaService.findMpa(mpaId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Collection<Mpa> findAll() {
        return mpaService.findAll();
    }
}
