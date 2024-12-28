package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    public final Map<Integer, Film> filmMap = new HashMap<>();
    public final Map<Integer, List<Integer>> filmLikes = new HashMap<>();

    @Override
    public Collection<Film> getAll() {
        return filmMap.values();
    }

    @Override
    public Film get(long id) {
        Film film = filmMap.get((int) id);
        return film;
    }

    @Override
    public Film create(Film film) {
        filmLikes.putIfAbsent(film.getId(), new ArrayList<>());
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

    @Override
    public Film setLike(Integer id, Integer userId) {
        filmLikes.get(id).add(userId);
        filmMap.get(id).setRating(filmMap.get(id).getRating() + 1);
        Film film = filmMap.get(id);
        return film;
    }

    @Override
    public Film deleteLike(Integer id, Integer userId) {
        filmLikes.get(id).remove(userId);
        filmMap.get(id).setRating(filmMap.get(id).getRating() - 1);
        Film film = filmMap.get(id);
        return film;
    }
}
