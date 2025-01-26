package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(int id) {
        Optional<Genre> genre = genreDbStorage.getGenreById(id);
        if (genre.isEmpty()) {
            throw new NotFoundException(String.format("Genre with id = %s not found", id));
        }
        return genre.orElse(null);
    }

    public List<Genre> getGenre() {
        return genreDbStorage.getGenre();
    }
}