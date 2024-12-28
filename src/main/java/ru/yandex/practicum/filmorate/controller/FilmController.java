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

    @PutMapping("/{id}/like/{userId}")
    public Film setlike(@PathVariable long id, @PathVariable long userId) {
        log.info("setlike started: id " + id + "userId -" + userId);
        Film film = filmService.setLike(id, userId);
        log.info("setlike finished: " + film.toString());
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("deleteLike started: id " + id + "userId -" + userId);
        Film film = filmService.deleteLike(id, userId);
        log.info("deleteLike finished: " + film.toString());
        return film;
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam (required = false) final Integer count) {
        log.info("getPopular started: count " + count);
        Collection<Film> listFilm = filmService.getPopular(count);
        log.info("getPopular finished: " + listFilm.toString());
        return listFilm;
    }
}
