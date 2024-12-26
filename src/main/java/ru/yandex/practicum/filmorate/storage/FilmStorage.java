package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface FilmStorage {
    public Collection<Film> getAll();
    public Film get();
    public Film create(Film film);
    public Film update(Film film);
}
