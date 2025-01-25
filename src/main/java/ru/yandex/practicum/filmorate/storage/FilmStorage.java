package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    public Collection<Film> getAll();

    public Optional<Film> get(long id);

    public Film create(Film film);

    public Film update(Film film);

    public Optional<Film> setLike(Integer id, Integer userId);

    public Optional<Film> deleteLike(Integer id, Integer userId);
}
