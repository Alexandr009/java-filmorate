package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    //Autowired
    private FilmDbStorage filmDbStorage;
    //Autowired
    private UserDbStorage userDbStorage;
    private MpaDbStorage mpaDbStorage;
    private GenreDbStorage genreDbStorage;

    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage, FilmDbStorage filmDbStorage, UserDbStorage userDbStorage, MpaDbStorage mpaDbStorage,GenreDbStorage genreDbStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;

        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public Collection<Film> findAll() {
        return filmDbStorage.getAll();
    }

    public Optional<Film> getFilmById(long id){
        return filmDbStorage.get(id);
    }

    public Film create(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);
        //film.setId((int) getNextId());
        film.setRating(0);
        //inMemoryFilmStorage.create(film);
        Optional<Mpa> mpa = mpaDbStorage.getMpaById(film.getMpa().getId());
        if (mpa.isEmpty()) {
            throw new ValidationException(String.format("mpa with id = %s not found", film.getMpa().getId()));
        }
        Optional<Genre> genre = genreDbStorage.getGenreById(film.getGenres().getFirst().getId());
        if (genre.isEmpty()) {
            throw new ValidationException(String.format("genre with id = %s not found", film.getMpa().getId()));
        }
        filmDbStorage.create(film);
        return film;
    }

    public Film update(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);

        if (film.getId() == null || film.getId().toString().isEmpty()) {
            throw new ConditionsNotMetException("ID must be specified");
        }

//        if (inMemoryFilmStorage.filmMap.containsKey(film.getId())) {
//            return inMemoryFilmStorage.update(film);
//        }

        Optional<Film> newFilm = filmDbStorage.get(film.getId());
        if (!newFilm.isEmpty()) {
            return filmDbStorage.update(film);
        }

        throw new NotFoundException(String.format("Film with ID = %s not found", film.getId()));
    }

    public Optional<Optional<Film>> setLike(long filmId, long id) {
        //Optional<User> userMain = inMemoryUserStorage.get(id);
        //Optional<Film> film = inMemoryFilmStorage.get(filmId);

        Optional<User> userMain = userDbStorage.get(id);
        Optional<Film> film = filmDbStorage.get(filmId);

        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Film with ID = %s not found", filmId));
        }

//        List<Integer> listFilmLikes = inMemoryFilmStorage.filmLikes.get((int) filmId);
//        if (listFilmLikes.isEmpty() || listFilmLikes.stream().noneMatch(like -> like == id)) {
//            inMemoryFilmStorage.setLike((int) filmId, (int) id);
//        }
//
//         return inMemoryFilmStorage.get(filmId);
        return Optional.ofNullable(filmDbStorage.setLike((int) filmId, (int) id));
    }

    public Optional<Optional<Film>> deleteLike(long filmId, long id) {
        //Optional<User> userMain = inMemoryUserStorage.get(id);
        //Optional<Film> film = inMemoryFilmStorage.get(filmId);
        Optional<User> userMain = userDbStorage.get(id);
        Optional<Film> film = filmDbStorage.get(filmId);
        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Film with ID = %s not found", filmId));
        }
        return Optional.ofNullable(filmDbStorage.deleteLike((int) filmId, (int) id));
//        List<Integer> listFilmLikes = inMemoryFilmStorage.filmLikes.get((int) filmId);
//        if (!listFilmLikes.isEmpty() && listFilmLikes.stream().anyMatch(like -> like == id)) {
//            inMemoryFilmStorage.deleteLike((int) filmId, (int) id);
//        }
//
//        return inMemoryFilmStorage.get(filmId);
    }

    public Collection<Film> getPopular(Integer count) {
//        if (count == null || count <= 0) {
//            count = 10;
//        }
        return filmDbStorage.getPopularFilms(count);

//        return inMemoryFilmStorage.getAll().stream()
//                .sorted((film1, film2) -> Integer.compare(film2.getRating(), film1.getRating()))
//                .limit(count)
//                .toList();
    }

    private long getNextId() {
        return inMemoryFilmStorage.filmMap.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
