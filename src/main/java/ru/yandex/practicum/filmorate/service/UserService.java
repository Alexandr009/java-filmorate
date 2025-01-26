package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {

    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public Collection<User> findAll() {
        return userDbStorage.getAll();
    }

    public Optional<User> getUserById(long id) {
        return userDbStorage.get(id);
    }

    public User create(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }

        return userDbStorage.creat(user);
    }

    public User update(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        if (user.getId() == null || user.getId().toString().isBlank()) {
            throw new ConditionsNotMetException("ID must be specified");
        }

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        Optional<User> existingUser = userDbStorage.get(user.getId());
        if (existingUser.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", user.getId()));
        }

        return userDbStorage.update(user);
    }

    public void addFriends(long id, long friendId) {
        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        userDbStorage.addFriends((int) id, (int) friendId);
    }

    public void deleteFriends(long id, long friendId) {
        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        userDbStorage.deleteFriends((int) friendId, (int) id);
    }

    public Collection<User> getFriends(long id) {
        Optional<User> userMain = userDbStorage.get(id);
        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        return userDbStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(long id, long friendId) {
        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        return userDbStorage.getCommonFriends((int) id, (int) friendId);
    }
}