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

    @Autowired
    public final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        Collection<Film> listFilm = filmService.findAll();
        log.info(String.format("findAll film finished - %s", listFilm.toString()));
        return listFilm;
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ParseException {
        log.info(String.format("create film started - %s", String.valueOf(film)));
        Film newFilm = filmService.create(film);
        log.info(String.format("create film finished - %s", String.valueOf(newFilm)));
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ParseException {
        log.info(String.format("update film started - %s", String.valueOf(film)));
        Film newFilm = filmService.update(film);
        log.info(String.format("update film finished - %s", String.valueOf(newFilm)));
        return newFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setlike(@PathVariable long id, @PathVariable long userId) {
        log.info(String.format("setlike started: id %d userId - %d", id, userId));
        Film film = filmService.setLike(id, userId);
        log.info(String.format("setlike finished: %s", film.toString()));
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info(String.format("deleteLike started: id %d userId - %d", id, userId));
        Film film = filmService.deleteLike(id, userId);
        log.info(String.format("deleteLike finished: %s", film.toString()));
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(required = false) final Integer count) {
        log.info(String.format("getPopular started: count %s", count));
        Collection<Film> listFilm = filmService.getPopular(count);
        log.info(String.format("getPopular finished: %s", listFilm.toString()));
        return listFilm;
    }
}