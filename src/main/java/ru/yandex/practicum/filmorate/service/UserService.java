package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<User> findAll() {
        return inMemoryUserStorage.getAll();
    }

    public User create(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        user.setId((int) getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        inMemoryUserStorage.creat(user);
        return user;
    }

    public User update(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        if (user.getId() == null || user.getId().toString().isBlank()) {
            throw new ConditionsNotMetException("ID must be specified");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (inMemoryUserStorage.userMap.containsKey(user.getId())) {
            return inMemoryUserStorage.update(user);
        }
        throw new NotFoundException(String.format("User with id = %s not found", user.getId()));
    }

    public void addFriends(long id, long friendId) {
        User userMain = inMemoryUserStorage.get(id);
        User userFriend = inMemoryUserStorage.get(friendId);

        if (userFriend == null) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        addFriendToList(id, friendId, userFriend);
        addFriendToList(friendId, id, userMain);
    }

    private void addFriendToList(long id, long friendId, User friend) {
        List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int) id);
        if (listUserFriends != null) {
            boolean friendExists = listUserFriends.stream()
                    .anyMatch(friendObj -> friendObj.getId() == friendId);
            if (!friendExists) {
                inMemoryUserStorage.addFriends((int) id, friend);
            }
        }
    }

    public void deleteFriends(long id, long friendId) {
        User userMain = inMemoryUserStorage.get(id);
        User userFriend = inMemoryUserStorage.get(friendId);

        if (userMain == null) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }
        deleteFriendFromList(id, friendId, userFriend);

        if (userFriend == null) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }
        deleteFriendFromList(friendId, id, userMain);
    }

    private void deleteFriendFromList(long id, long friendId, User friend) {
        List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int) id);
        if (listUserFriends != null) {
            boolean friendExists = listUserFriends.stream()
                    .anyMatch(friendObj -> friendObj.getId() == friendId);
            if (friendExists) {
                inMemoryUserStorage.deleteFriends((int) id, friend);
            }
        }
    }

    public Collection<User> getFriends(long id) {
        User userMain = inMemoryUserStorage.get(id);
        if (userMain == null) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }
        Collection<User> listUserFriends = inMemoryUserStorage.userFriends.get((int) id);

        return listUserFriends;
    }

    public Collection<User> getCommonFriends(long id, long friendId) {
        User userMain = inMemoryUserStorage.get(id);
        User userFriend = inMemoryUserStorage.get(friendId);

        if (userMain == null) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }
        if (userFriend == null) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        List<User> listUser1 = inMemoryUserStorage.userFriends.get((int) id);
        List<User> listUser2 = inMemoryUserStorage.userFriends.get((int) friendId);

        if (listUser1 == null || listUser2 == null) {
            return new ArrayList<>();
        }

        return listUser1.stream()
                .filter(listUser2::contains)
                .toList();
    }

    private long getNextId() {
        return inMemoryUserStorage.userMap.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
