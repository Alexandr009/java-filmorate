package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;

@Service
public class FilmService {
    //добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
    // Пока пусть каждый пользователь может поставить лайк фильму только один раз.

    @Autowired
    public final InMemoryFilmStorage inMemoryFilmStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.getAll();
    }

    public Film create(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);
        film.setId((int) this.getNextId());
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
