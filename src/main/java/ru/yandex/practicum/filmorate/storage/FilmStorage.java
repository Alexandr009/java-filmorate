package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getAll();
    public Film get(long id);
    public Film create(Film film);
    public Film update(Film film);
    public Film setLike(Integer id, Integer userId);
    public Film deleteLike(Integer id, Integer userId);
}
