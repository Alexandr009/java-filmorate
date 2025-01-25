package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public Optional<Optional<Film>> getFilmById(@PathVariable long id) {
        log.info("getFilmById started - %s - " + id);
        return Optional.ofNullable(filmService.getFilmById(id));
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

    @PutMapping("/{film-id}/like/{user-id}")
    public void setlike(@PathVariable("film-id") long filmId, @PathVariable("user-id") long userId) {
        log.info(String.format("setlike started: film_id %d id - %d", filmId, userId));
        Optional<Optional<Film>> film = filmService.setLike(filmId, userId);
        log.info(String.format("setlike finished: %s", film.get().toString()));
    }

    @DeleteMapping("/{film-id}/like/{user-id}")
    public void deleteLike(@PathVariable("film-id") long filmId, @PathVariable("user-id") long userId) {
        log.info(String.format("deleteLike started: id %d userId - %d", filmId, userId));
        Optional<Optional<Film>> film = filmService.deleteLike(filmId, userId);
        log.info(String.format("deleteLike finished: %s", film.get().toString()));
    }

    @GetMapping("/popular")
    //public Collection<Film> getPopular(@RequestParam(required = false) final Integer count) {
    public Collection<Film> popularFilms(@RequestParam(defaultValue = "10") String count) {
        log.info(String.format("getPopular started: count %s", count));
        Collection<Film> listFilm = filmService.getPopular(Integer.parseInt(count));
        log.info(String.format("getPopular finished: %s", listFilm.toString()));
        return listFilm;
    }
}