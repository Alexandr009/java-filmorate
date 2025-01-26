package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserDbStorage userDbStorage;
    @Autowired
    public UserService(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public Collection<User> findAll() {
        return userDbStorage.getAll();
    }

    public Optional<User> getUserById(long id){
        return userDbStorage.get(id);
    }
    public User create(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        User newUser = userDbStorage.creat(user);
        return newUser;
    }



    public User update(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        if (user.getId() == null || user.getId().toString().isBlank()) {
            throw new ConditionsNotMetException("ID must be specified");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        Optional<User> newUser = userDbStorage.get(user.getId());
        if (!newUser.isEmpty()) {
            return userDbStorage.update(user);
        }

        throw new NotFoundException(String.format("User with id = %s not found", user.getId()));
    }

    public void addFriends(long id, long friendId) {

        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        userDbStorage.addFriends((int) id,(int) friendId);
    }


    public void deleteFriends(long id, long friendId) {

        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }

        userDbStorage.deleteFriends((int) friendId,(int) id);

        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

    }

    public Collection<User> getFriends(long id) {
        Optional<User> userMain = userDbStorage.get(id);
        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }
        Collection<User> listUserFriends = userDbStorage.getFriends(id);

        return listUserFriends;
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

        return userDbStorage.getCommonFriends((int) id,(int) friendId);
    }

}
