package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mpa findMpaByID(@PathVariable(value = "id") int mpaId) {
        log.info("Get mpa id={}", mpaId);
        return mpaService.findMpa(mpaId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Collection<Mpa> findAll() {
        log.info("Get all mpa");
        return mpaService.findAll();
    }
}
