package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    public final Map<Integer, Film> filmMap = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return filmMap.values();
    }

    @Override
    public Film get() {
        return null;
    }

    @Override
    public Film create(Film film) {
        return filmMap.put(film.getId(), film);
    }

    @Override
    public Film update(Film film) {
        Film oldFilm = filmMap.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setReleaseDate(film.getReleaseDate());
        return oldFilm;
    }
}
