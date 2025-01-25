package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component("MpaDbStorage")
@Slf4j
@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        this.mapper = mapper;
        this.jdbc = jdbc;
    }

    public Optional<Mpa> getMpaById(int id) {
        String sqlRequest = "SELECT * FROM mpa WHERE id = ?";
        try {
            Mpa result = jdbc.queryForObject(sqlRequest, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }

    }

    public List<Mpa> getMpa() {
        String sqlRequest = "SELECT * FROM mpa";
        //return jdbc.query(sqlRequest, (rs, rowNum) -> makeMpa(rs));
        List<Mpa> results = jdbc.query(sqlRequest, mapper);
        return results;
    }


}
