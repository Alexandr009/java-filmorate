package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component("UserDbStorage")
@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        this.mapper = mapper;
        this.jdbc = jdbc;
    }


    @Override
    public User creat(User user) {
        String sqlCreateUser = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?,?,?,?)";
        jdbc.update(sqlCreateUser,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        String sqlWhereIdUser = "SELECT id FROM users WHERE email = ?";
        String query = "SELECT * FROM users WHERE email = ?";
        User result = jdbc.queryForObject(query, mapper, user.getEmail());

        return result;
    }

    @Override
    public Optional<User> get(long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try {
            User result = jdbc.queryForObject(query, mapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getAll() {
        String sqlRequest = "SELECT * FROM users";
        List<User> results = jdbc.query(sqlRequest, mapper);
        return results;
        //return List.of();
    }

    @Override
    public User update(User user) {
        String sqlUpdateUser = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id= ?";
        jdbc.update(sqlUpdateUser,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        String query = "SELECT * FROM users WHERE id = ?";
        User result = jdbc.queryForObject(query, mapper, user.getId());
        return result;
    }

    @Override
    public void addFriends(Integer userId, User friend) {

    }

    @Override
    public void deleteFriends(Integer userId, User friend) {

    }

    @Override
    public Collection<User> getFriends(long id) {
        return List.of();
    }

}
