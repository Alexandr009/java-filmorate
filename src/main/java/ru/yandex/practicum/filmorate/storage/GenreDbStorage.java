package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Component("GenreDbStorage")
@Slf4j
@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        this.mapper = mapper;
        this.jdbc = jdbc;
    }

    public Optional<Genre> getGenreById(int id) {
        String sqlRequest = "SELECT * FROM GENRE WHERE id = ?";
        try {
            Genre result = jdbc.queryForObject(sqlRequest, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }

    }

    public List<Genre> getGenre() {
        String sqlRequest = "SELECT * FROM GENRE";
        List<Genre> results = jdbc.query(sqlRequest, mapper);
        return results;
    }


}
