package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.getAll();
    }

    public Film create(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);
        film.setId((int) getNextId());
        film.setRating(0);
        inMemoryFilmStorage.create(film);
        return film;
    }

    public Film update(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);

        if (film.getId() == null || film.getId().toString().isEmpty()) {
            throw new ConditionsNotMetException("ID must be specified");
        }

        if (inMemoryFilmStorage.filmMap.containsKey(film.getId())) {
            return inMemoryFilmStorage.update(film);
        }

        throw new NotFoundException(String.format("Film with ID = %s not found", film.getId()));
    }

    public Film setLike(long filmId, long id) {
        User userMain = inMemoryUserStorage.get(id);
        Film film = inMemoryFilmStorage.get(filmId);

        if (userMain == null) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (film == null) {
            throw new NotFoundException(String.format("Film with ID = %s not found", filmId));
        }

        List<Integer> listFilmLikes = inMemoryFilmStorage.filmLikes.get((int) filmId);
        if (listFilmLikes.isEmpty() || listFilmLikes.stream().noneMatch(like -> like == id)) {
            inMemoryFilmStorage.setLike((int) filmId, (int) id);
        }

         return inMemoryFilmStorage.get(filmId);
    }

    public Film deleteLike(long filmId, long id) {
        User userMain = inMemoryUserStorage.get(id);
        Film film = inMemoryFilmStorage.get(filmId);

        if (userMain == null) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (film == null) {
            throw new NotFoundException(String.format("Film with ID = %s not found", filmId));
        }

        List<Integer> listFilmLikes = inMemoryFilmStorage.filmLikes.get((int) filmId);
        if (!listFilmLikes.isEmpty() && listFilmLikes.stream().anyMatch(like -> like == id)) {
            inMemoryFilmStorage.deleteLike((int) filmId, (int) id);
        }

        return inMemoryFilmStorage.get(filmId);
    }

    public Collection<Film> getPopular(Integer count) {
        if (count == null || count <= 0) {
            count = 10;
        }

        return inMemoryFilmStorage.getAll().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getRating(), film1.getRating()))
                .limit(count)
                .toList();
    }

    private long getNextId() {
        return inMemoryFilmStorage.filmMap.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
