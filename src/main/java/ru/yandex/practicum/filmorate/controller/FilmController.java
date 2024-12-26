package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.text.ParseException;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    //public final Map<Integer, Film> films = new HashMap<>();
    @Autowired
    public final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        Collection<Film> listFilm = filmService.findAll();
        log.info("findAll film finished - " + listFilm.toString());
        return listFilm;
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ParseException {
        log.info("create film started - " + String.valueOf(film));
        Film newFilm = filmService.create(film);
        log.info("create film finished - " + String.valueOf(newFilm));
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ParseException {
        log.info("update film started - " + String.valueOf(film));
        Film newFilm = filmService.update(film);
        log.info("update film finished - " + String.valueOf(newFilm));
        return newFilm;
    }
}
