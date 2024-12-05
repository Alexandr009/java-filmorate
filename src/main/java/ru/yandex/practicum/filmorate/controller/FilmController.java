package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info(films.values().toString());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ParseException {
        log.info(String.valueOf(film));
        ValidationUtils.validateFilm(film);

        film.setId((int) this.getNextId());
        films.put(film.getId(), film);
        log.info(String.valueOf(film));
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ParseException {
        log.info(String.valueOf(film));
        ValidationUtils.validateFilm(film);

        if (film.getId() == null || film.getId().toString().equals("")) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setDuration(film.getDuration());
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.info(String.valueOf(oldFilm));
            return oldFilm;
        }

        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
