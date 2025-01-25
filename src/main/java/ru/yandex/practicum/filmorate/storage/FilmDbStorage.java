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
//        String query = "SELECT * FROM FILMS";
//        List<Film> results = jdbc.query(query, mapper);
//        return results;
        String sqlRequest = "SELECT f.id,\n" +
                "       f.name,\n" +
                "       f.description,\n" +
                "       f.release_date,\n" +
                "       f.duration,\n" +
                "       f.id_mpa,\n" +
                "       mp.name\n" +
                "FROM films AS f\n" +
                "INNER JOIN mpa AS mp on mp.id = f.id_mpa";
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
                "INNER JOIN mpa AS m ON m.id = fi.id_mpa " +
                "WHERE fi.id = ?";

        List<Film> films = jdbc.query(sqlRequest, mapper, id);

        if (films.isEmpty()) {
            return Optional.empty(); // Если фильм не найден
        }

        // Получаем единственный фильм
        Film film = films.get(0);
        ///
    // Создайте объект Mpa, если он существует
// Если id_mpa не null (не 0), то создаем объект Mpa и добавляем его в фильм
        if (film.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(film.getMpa().getId()); // Извлекаем mpa_id
            mpa.setName(film.getMpa().getName()); // Извлекаем mpa_name
            film.setMpa(mpa);
        } else {
            film.setMpa(null); // Если mpa отсутствует, устанавливаем null
        }
        //

        // SQL-запрос для получения жанров фильма
        String genreQuery = "SELECT g.id AS genre_id, " +
                "       g.name AS genre_name " +
                "FROM genre_films AS gf " +
                "INNER JOIN genre AS g ON gf.id_genre = g.id " +
                "WHERE gf.id_film = ?";

        List<Genre> genres = jdbc.query(genreQuery, (rs, rowNum) -> {
            Genre genre = new Genre(); // Используем конструктор без параметров
            genre.setId(rs.getInt("genre_id")); // Устанавливаем id жанра
            genre.setName(rs.getString("genre_name")); // Устанавливаем имя жанра
            return genre;
        }, id);

        // Добавляем жанры к фильму
        film.setGenres(genres);

        return Optional.of(film);
        /*
            String sqlRequest = "SELECT fi.id, " +
                    "       fi.name, " +
                    "       fi.description, " +
                    "       fi.release_date, " +
                    "       fi.duration, " +
                    "       fi.id_mpa, " +
                    "       m.id AS mpa_id, " +
                    "       m.name AS mpa_name " +
                    "FROM films AS fi " +
                    "INNER JOIN mpa AS m ON m.id = fi.id_mpa " +
                    "WHERE fi.id = ?";

            List<Film> films = jdbc.query(sqlRequest, mapper, id);

            if (films.isEmpty()) {
                return Optional.empty(); // Если фильм не найден
            }

            // Получаем единственный фильм
            Film film = films.get(0);

            // SQL-запрос для получения жанров фильма
            String genreQuery = "SELECT g.id AS genre_id, " +
                    "       g.name AS genre_name " +
                    "FROM genre_films AS gf " +
                    "INNER JOIN genre AS g ON gf.id_genre = g.id " +
                    "WHERE gf.id_film = ?";

            List<Genre> genres = jdbc.query(genreQuery, (rs, rowNum) -> new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("genre_name")
            ), id);

            // Добавляем жанры к фильму
            film.setGenres(genres);

            return Optional.of(film);
*/
    }

    @Override
    public Film create(Film film) {
        List<Genre> newListGenre = new ArrayList<>();
        String sqlRequest = "INSERT INTO films (name, description, release_date, duration, id_mpa)" +
                "VALUES(?,?,?,?,?)";
        jdbc.update(sqlRequest,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        SqlRowSet sqlRowSet = jdbc.queryForRowSet("SELECT ID FROM FILMS WHERE NAME = ?", film.getName());
        if (sqlRowSet.next()) {
            film.setId((int) sqlRowSet.getLong("id"));
        }
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
        String sqlRequest = "INSERT INTO likes_films (id_films, id_user)\n" +
                "VALUES (?,?)";
        jdbc.update(sqlRequest, id, userId);
        Optional<Film> filmToReturn = get(id);
        return filmToReturn;
    }

    @Override
    public Optional<Film> deleteLike(Integer id, Integer userId) {
        String sqlRequest = "DELETE FROM likes_films WHERE id_films = ? AND id_user = ?";
        jdbc.update(sqlRequest, id, userId);
        Optional<Film> filmToReturn = get(id);
        return filmToReturn;
    }
    private List<Genre> setGenres(Film film) {
        List<Genre> newListGenre = new ArrayList<>();
        String sqlRequestForGenreCreate = "INSERT INTO GENRE_FILMS (ID_FILM, ID_GENRE) VALUES ( ?,? )";
        for (Genre genre : film.getGenres()) {
            if (checkGenreIDTable(film.getId(), genre.getId())) {
                jdbc.update(sqlRequestForGenreCreate,
                        film.getId(),
                        genre.getId());
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
        for (Integer filmsId : filmsIds) {
            if (filmsId == idGenres) {
                return false;
            }
        }
        return true;
    }

    public void setGenreToFilms(List<Film> films) {
        /*
        // Проверка на null или пустой список
        if (films == null || films.isEmpty()) {
            return;
        }

        // Создаем Map для быстрого поиска фильмов по их ID
        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        // Запрос для получения жанров для фильмов
        String sql = "SELECT f.id AS id_film, " +
                "       g.id AS id_genre, " +
                "       g.name AS name_genre " +
                "FROM films AS f " +
                "INNER JOIN genre_films AS gf ON f.id = gf.id_film " +
                "INNER JOIN genre AS g ON gf.id_genre = g.id " +
                "WHERE f.id IN (:filmIds)";

        // Получаем список ID фильмов для фильтрации
        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        // Выполняем запрос с фильтрацией по ID фильмов
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);

        SqlRowSet sqlRowSetGenres;
        try {
            sqlRowSetGenres = jdbc.queryForRowSet(sql, parameters);
        } catch (DataAccessException e) {
            // Логируем и завершаем выполнение метода в случае ошибки
            System.err.println("Error executing SQL query: " + e.getMessage());
            return;
        }

        // Заполняем жанры для фильмов
        while (sqlRowSetGenres.next()) {
            int filmId = sqlRowSetGenres.getInt("id_film");
            Film film = filmMap.get(filmId); // Ищем фильм по ID в Map

            if (film != null) {
                Genre genre = new Genre(
                        sqlRowSetGenres.getInt("id_genre"),
                        sqlRowSetGenres.getString("name_genre")
                );
                film.getGenres().add(genre);
            }
        }*/

        if (films == null || films.isEmpty()) {
            return;
        }

        // Создаем Map для быстрого поиска фильмов по их ID
        Map<Integer, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        // Запрос для получения жанров для фильмов
        String sql = "SELECT f.id AS id_film, " +
                "       g.id AS id_genre, " +
                "       g.name AS name_genre " +
                "FROM films AS f " +
                "INNER JOIN genre_films AS gf ON f.id = gf.id_film " +
                "INNER JOIN genre AS g ON gf.id_genre = g.id " +
                "WHERE f.id IN (:filmIds)";

        // Получаем список ID фильмов для фильтрации
        List<Integer> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        // Выполняем запрос с фильтрацией по ID фильмов
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("filmIds", filmIds);

        SqlRowSet sqlRowSetGenres;
        try {
            sqlRowSetGenres = jdbc.queryForRowSet(sql, parameters);
        } catch (DataAccessException e) {
            // Логируем и завершаем выполнение метода в случае ошибки
            System.err.println("Error executing SQL query: " + e.getMessage());
            return;
        }

        // Заполняем жанры для фильмов
        while (sqlRowSetGenres.next()) {
            int filmId = sqlRowSetGenres.getInt("id_film");
            Film film = filmMap.get(filmId); // Ищем фильм по ID в Map

            if (film != null) {
                Genre genre = new Genre(); // Используем конструктор без параметров
                genre.setId(sqlRowSetGenres.getInt("id_genre")); // Устанавливаем id жанра
                genre.setName(sqlRowSetGenres.getString("name_genre")); // Устанавливаем имя жанра
                film.getGenres().add(genre); // Добавляем жанр в список жанров фильма
            }
        }
    }

    public List<Film> getPopularFilms(int count) {
        String sqlRequest = "SELECT fi.id,\n" +
                "       fi.name,\n" +
                "       fi.description,\n" +
                "       fi.release_date,\n" +
                "       fi.duration,\n"+
                "       fi.id_mpa,\n" +
                "       m.name\n" +
                "FROM films AS fi\n" +
                "INNER JOIN likes_films AS lf on fi.id = lf.id_films\n" +
                "INNER JOIN mpa AS m on fi.id_mpa = m.id\n" +
                "GROUP BY fi.id\n" +
                "ORDER BY COUNT(lf.id_films) DESC\n" +
                "LIMIT ?";
        //Collection<Film> films = jdbc.query(sqlRequest, mapper);
        Collection<Film> films = jdbc.query(sqlRequest, new Object[]{count}, mapper);
        setGenreToFilms((List<Film>) films);
        if(films.isEmpty()) {
            films = getAll();
        }
        return (List<Film>) films;
    }
}
