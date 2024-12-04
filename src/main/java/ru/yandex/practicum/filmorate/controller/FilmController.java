package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    public final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll(){
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ParseException {
        validationJson(film);
        film.setId((int) this.getNextId());
        films.put(film.getId(),film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ParseException {
        validationJson(film);
        if (film.getId().toString() == "" || film.getId() == null){
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setDuration(film.getDuration());
            oldFilm.setReleaseDate(film.getReleaseDate());
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    //проверка входящего запроса
    public void validationJson(Film film) throws ParseException {
        String dateString = "1895-12-28";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateCheck = format.parse(dateString);

        if(film.getName().isBlank()){
            throw new ValidationException("название не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().before(dateCheck)) {
            throw new ValidationException("дата релиза — не модет быть раньше 28 декабря 1895 года");
        } else if(film.getDuration().isNegative()){
            throw new ValidationException("продолжительность фильма должна быть положительным числом.");
        }
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
