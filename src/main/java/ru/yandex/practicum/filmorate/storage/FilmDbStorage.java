package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

@Component("FilmServiceDb")
@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        this.mapper = mapper;
        this.jdbc = jdbc;
    }

    @Override
    public Collection<Film> getAll() {
        String sqlRequest = "SELECT f.id, " +
                "       f.name, " +
                "       f.description, " +
                "       f.release_date, " +
                "       f.duration, " +
                "       f.id_mpa, " +
                "       mp.name AS mpa_name " +
                "FROM films AS f " +
                "INNER JOIN mpa AS mp ON mp.id = f.id_mpa";

        List<Film> resultFilmList = jdbc.query(sqlRequest, mapper);
        setGenreToFilms(resultFilmList);
        return resultFilmList;
    }

    @Override
    public Optional<Film> get(long id) {
        String sqlRequest = "SELECT fi.id, " +
                "       fi.name, " +
                "       fi.description, " +
                "       fi.release_date, " +
                "       fi.duration, " +
                "       fi.id_mpa, " +
                "       m.id AS mpa_id, " +
                "       m.name AS mpa_name " +
                "FROM films AS fi " +
                "INNER JOIN mpa AS m ON fi.id_mpa = m.id " +
                "WHERE fi.id = ?";

        List<Film> films = jdbc.query(sqlRequest, mapper, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);

        if (film.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(film.getMpa().getId());
            mpa.setName(film.getMpa().getName());
            film.setMpa(mpa);
        } else {
            film.setMpa(null);
        }

        String genreQuery = "SELECT g.id AS genre_id, " +
                "       g.name AS genre_name " +
                "FROM genre_films AS gf " +
                "INNER JOIN genre AS g ON gf.id_genre = g.id " +
                "WHERE gf.id_film = ?";

        List<Genre> genres = jdbc.query(genreQuery, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        }, id);

        film.setGenres(genres);

        return Optional.of(film);
    }

    @Override
    public Film create(Film film) {
        List<Genre> newListGenre = new ArrayList<>();
        String sqlRequest = "INSERT INTO films (name, description, release_date, duration, id_mpa) " +
                "VALUES(?,?,?,?,?)";

        jdbc.update(sqlRequest,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        SqlRowSet sqlRowSet = jdbc.queryForRowSet(
                "SELECT ID " +
                        "FROM FILMS " +
                        "WHERE NAME = ? " +
                        "AND description = ? " +
                        "AND id_mpa = ?",
                film.getName(),
                film.getDescription(),
                film.getMpa().getId()
        );

        if (sqlRowSet.next()) {
            film.setId((int) sqlRowSet.getLong("id"));
        }

        String sqlRequestForGenreDelete = "DELETE FROM GENRE_FILMS WHERE ID_FILM = ?";
        jdbc.update(sqlRequestForGenreDelete, film.getId());
        if (film.getGenres() != null) {
            newListGenre = setGenres(film);
        }

        Film filmToReturn = new Film();
        filmToReturn.setId(film.getId());
        filmToReturn.setName(film.getName());
        filmToReturn.setDescription(film.getDescription());
        filmToReturn.setReleaseDate(film.getReleaseDate());
        filmToReturn.setDuration(film.getDuration());
        filmToReturn.setMpa(film.getMpa());
        filmToReturn.setGenres(newListGenre);

        return filmToReturn;
    }

    @Override
    public Film update(Film film) {
        List<Genre> newListGenre = new ArrayList<>();
        String sqlRequest = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, id_mpa = ? " +
                "WHERE id = ?";

        jdbc.update(sqlRequest,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        String sqlRequestForGenreDelete = "DELETE FROM GENRE_FILMS WHERE ID_FILM = ?";
        jdbc.update(sqlRequestForGenreDelete, film.getId());

        if (film.getGenres() != null) {
            newListGenre = setGenres(film);
        }

        Film filmToReturn = new Film();
        filmToReturn.setId(film.getId());
        filmToReturn.setName(film.getName());
        filmToReturn.setDescription(film.getDescription());
        filmToReturn.setReleaseDate(film.getReleaseDate());
        filmToReturn.setDuration(film.getDuration());
        filmToReturn.setMpa(film.getMpa());
        filmToReturn.setGenres(newListGenre);

        return filmToReturn;
    }

    @Override
    public Optional<Film> setLike(Integer id, Integer userId) {
        String sqlRequest = "INSERT INTO likes_films (id_films, id_user) VALUES (?,?)";
        jdbc.update(sqlRequest, id, userId);
        return get(id);
    }

    @Override
    public Optional<Film> deleteLike(Integer id, Integer userId) {
        String sqlRequest = "DELETE FROM likes_films WHERE id_films = ? AND id_user = ?";
        jdbc.update(sqlRequest, id, userId);
        return get(id);
    }

    private List<Genre> setGenres(Film film) {
        List<Genre> newListGenre = new ArrayList<>();
        String sqlRequestForGenreCreate = "INSERT INTO GENRE_FILMS (ID_FILM, ID_GENRE) VALUES (?,?)";

        for (Genre genre : film.getGenres()) {
            boolean isDuplicate = newListGenre.stream()
                    .anyMatch(g -> g.getId() == genre.getId());

            if (!isDuplicate && checkGenreIDTable(film.getId(), genre.getId())) {
                jdbc.update(sqlRequestForGenreCreate, film.getId(), genre.getId());
                newListGenre.add(genre);
            }
        }
        return newListGenre;
    }

    private boolean checkGenreIDTable(long idFilm, int idGenres) {
        List<Integer> filmsIds = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbc.queryForRowSet("SELECT ID_GENRE FROM GENRE_FILMS WHERE ID_FILM = ?", idFilm);

        while (sqlRowSet.next()) {
            filmsIds.add(sqlRowSet.getInt("id_genre"));
        }

        return filmsIds.stream().noneMatch(filmId -> filmId == idGenres);
    }

    public void setGenreToFilms(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        String sql = "SELECT f.id AS id_film, " +
                "       g.id AS id_genre, " +
                "       g.name AS name_genre " +
                "FROM films AS f " +
                "INNER JOIN genre_films AS gf ON f.id = gf.id_film " +
                "INNER JOIN genre AS g ON gf.id_genre = g.id " +
                "WHERE f.id IN (:filmIds)";

        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);

        SqlRowSet sqlRowSetGenres;
        try {
            sqlRowSetGenres = jdbc.queryForRowSet(sql, parameters);
        } catch (DataAccessException e) {
            log.error("Error executing SQL query: " + e.getMessage());
            return;
        }

        while (sqlRowSetGenres.next()) {
            int filmId = sqlRowSetGenres.getInt("id_film");
            Film film = filmMap.get(filmId);

            if (film != null) {
                Genre genre = new Genre();
                genre.setId(sqlRowSetGenres.getInt("id_genre"));
                genre.setName(sqlRowSetGenres.getString("name_genre"));
                film.getGenres().add(genre);
            }
        }
    }

    public List<Film> getPopularFilms(int count) {
        String sqlRequest = "SELECT fi.id, " +
                "       fi.name, " +
                "       fi.description, " +
                "       fi.release_date, " +
                "       fi.duration, " +
                "       fi.id_mpa, " +
                "       m.name AS mpa_name " +
                "FROM films AS fi " +
                "INNER JOIN likes_films AS lf ON fi.id = lf.id_films " +
                "INNER JOIN mpa AS m ON fi.id_mpa = m.id " +
                "GROUP BY fi.id " +
                "ORDER BY COUNT(lf.id_films) DESC " +
                "LIMIT ?";

        Collection<Film> films = jdbc.query(sqlRequest, new Object[]{count}, mapper);
        setGenreToFilms((List<Film>) films);

        if (films.isEmpty()) {
            films = getAll();
        }

        return (List<Film>) films;
    }
}
