package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
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
        String query = "SELECT * FROM users WHERE email = ?";
        return jdbc.queryForObject(query, mapper, user.getEmail());
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
        return jdbc.query(sqlRequest, mapper);
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
        return jdbc.queryForObject(query, mapper, user.getId());
    }

    public void addFriends(Integer userId, Integer friendId) {
        String sqlRequest = "INSERT INTO friends (id_user, id_friends, friendship_status) " +
                "VALUES(?,?,?)";
        jdbc.update(sqlRequest, userId, friendId, false);
        jdbc.update(sqlRequest, friendId, userId, true);
    }

    @Override
    public void deleteFriends(Integer userId, Integer friendId) {
        String sqlRequest = "DELETE FROM friends WHERE id_user = ? AND id_friends = ?";
        jdbc.update(sqlRequest, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long id) {
        String sqlRequest = "SELECT us.id,\n" +
                "       us.email,\n" +
                "       us.login,\n" +
                "       us.name,\n" +
                "       us.birthday\n" +
                "FROM users AS us\n" +
                "INNER JOIN friends AS f on us.id = f.id_user\n" +
                "WHERE f.friendship_status = true\n" +
                "        AND id_friends = ?";
        return jdbc.query(sqlRequest, mapper, id);
    }

    public List<User> getCommonFriends(long idUser, long idOtherUser) {
        String sqlRequest = "SELECT us.id,\n" +
                "       us.email,\n" +
                "       us.login,\n" +
                "       us.name,\n" +
                "       us.birthday\n" +
                "FROM users AS us\n" +
                "INNER JOIN friends AS f on us.id = f.id_user\n" +
                "WHERE us.id IN (SELECT fr.id_user\n" +
                "                        FROM friends AS fr\n" +
                "                        WHERE fr.id_friends = ?\n" +
                "                                AND fr.friendship_status = true)\n" +
                "        AND f.id_friends = ? AND friendship_status = true";
        return jdbc.query(sqlRequest, mapper, idUser, idOtherUser);
    }
}
