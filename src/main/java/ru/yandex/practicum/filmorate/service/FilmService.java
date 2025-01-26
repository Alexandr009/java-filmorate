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
import java.util.Optional;

@Service
public class FilmService {
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;
    private MpaDbStorage mpaDbStorage;
    private GenreDbStorage genreDbStorage;


    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, UserDbStorage userDbStorage, MpaDbStorage mpaDbStorage,GenreDbStorage genreDbStorage) {
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
        film.setRating(0);
        Optional<Mpa> mpa = mpaDbStorage.getMpaById(film.getMpa().getId());
        if (mpa.isEmpty()) {
            throw new ValidationException(String.format("mpa with id = %s not found", film.getMpa().getId()));
        }
//        Optional<Genre> genre = genreDbStorage.getGenreById(film.getGenres().getFirst().getId());
//        if (genre.isEmpty()) {
//            throw new ValidationException(String.format("genre with id = %s not found", film.getMpa().getId()));
//        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                Optional<Genre> genreOptional = genreDbStorage.getGenreById(genre.getId());
                if (genreOptional.isEmpty()) {
                    throw new ValidationException(String.format("Genre with id = %s not found", genre.getId()));
                }
            }
        }

        filmDbStorage.create(film);
        return film;
    }

    public Film update(Film film) throws ParseException {
        ValidationUtils.validateFilm(film);

        if (film.getId() == null || film.getId().toString().isEmpty()) {
            throw new ConditionsNotMetException("ID must be specified");
        }

        Optional<Film> newFilm = filmDbStorage.get(film.getId());
        if (!newFilm.isEmpty()) {
            return filmDbStorage.update(film);
        }

        throw new NotFoundException(String.format("Film with ID = %s not found", film.getId()));
    }

    public Optional<Optional<Film>> setLike(long filmId, long id) {

        Optional<User> userMain = userDbStorage.get(id);
        Optional<Film> film = filmDbStorage.get(filmId);

        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Film with ID = %s not found", filmId));
        }

        return Optional.ofNullable(filmDbStorage.setLike((int) filmId, (int) id));
    }

    public Optional<Optional<Film>> deleteLike(long filmId, long id) {;
        Optional<User> userMain = userDbStorage.get(id);
        Optional<Film> film = filmDbStorage.get(filmId);
        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Film with ID = %s not found", filmId));
        }
        return Optional.ofNullable(filmDbStorage.deleteLike((int) filmId, (int) id));
    }

    public Collection<Film> getPopular(Integer count) {
        return filmDbStorage.getPopularFilms(count);
    }

}
