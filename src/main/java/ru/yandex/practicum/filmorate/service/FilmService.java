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

    @Autowired
    public final InMemoryFilmStorage inMemoryFilmStorage;
    @Autowired
    public final InMemoryUserStorage inMemoryUserStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.getAll();
    }

    public Film create(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);
        film.setId((int) this.getNextId());
        film.setRating(0);
        inMemoryFilmStorage.create(film);
        return film;
    }

    public Film update(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);
        if (film.getId() == null || film.getId().toString().equals("")) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (inMemoryFilmStorage.filmMap.containsKey(film.getId())) {
            return inMemoryFilmStorage.update(film);
        }

        throw new NotFoundException(String.format("Фильм с id = %s не найден",film.getId()));
    }

    public Film setLike(long id, long userId) {
        User userMain = inMemoryUserStorage.get(userId);

        if (userMain == null) {
            throw new NotFoundException(String.format("User с id = %s не найден", id));
        } else {
            List<Integer> listFilmLikes = inMemoryFilmStorage.filmLikes.get((int) id);
            if (!listFilmLikes.isEmpty()) {
                Integer like = listFilmLikes.stream().filter(lik -> lik == userId)
                        .findFirst()
                        .orElse(null);
                if (like == null) {
                    inMemoryFilmStorage.setLike((int) id, (int) userId);
                }
            } else {
                inMemoryFilmStorage.setLike((int) id, (int) userId);
            }
        }

        return inMemoryFilmStorage.get(id);
    }

    public Film deleteLike(long id, long userId) {
        User userMain = inMemoryUserStorage.get(userId);

        if (userMain == null) {
            throw new NotFoundException(String.format("User с id = %s не найден", id));
        } else {
            List<Integer> listFilmLikes = inMemoryFilmStorage.filmLikes.get((int) id);
            if (!listFilmLikes.isEmpty()) {
                Integer like = listFilmLikes.stream().filter(lik -> lik == userId)
                        .findFirst()
                        .orElse(null);
                if (like != null) {
                    inMemoryFilmStorage.deleteLike((int) id, (int) userId);
                }
            }
        }
        return inMemoryFilmStorage.get(id);
    }

    public Collection<Film> getPopular(Integer count) {
        if (count == null || count <= 0) {
            count = 10;
        }
        Collection<Film> sortFilms = inMemoryFilmStorage.getAll().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getRating(), film1.getRating()))
                .limit(count)
                .toList();
        return sortFilms;
    }

    // вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = inMemoryFilmStorage.filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
