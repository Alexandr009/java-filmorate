package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private final InMemoryUserStorage inMemoryUserStorage;

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
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (inMemoryUserStorage.userMap.containsKey(user.getId())) {
            return inMemoryUserStorage.update(user);
        }
        throw new NotFoundException(String.format("User с id = %s не найден",user.getId()));
    }

    public void addFriends (long id, long friendId) {
        User userMain = inMemoryUserStorage.get(id);
        User userFriend = inMemoryUserStorage.get(friendId);

        if (userMain == null ) {
            throw new NotFoundException(String.format("User с id = %s не найден",id));
        } else {
            List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int)id);
            if (listUserFriends != null) {
                User userExist = listUserFriends.stream().filter(friendObjeck -> friendObjeck.getId() == friendId)
                        .findFirst()
                        .orElse(null);
                if (userExist == null) {
                    inMemoryUserStorage.addFriends((int) id, userFriend);
                }
            }
        }

        if (userFriend == null) {
            throw new NotFoundException(String.format("Friend с id = %s не найден",friendId));
        }else {
            List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int)friendId);
            if (listUserFriends != null) {
                User userExist = listUserFriends.stream().filter(friendObjeck -> friendObjeck.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (userExist == null) {
                    inMemoryUserStorage.addFriends((int) friendId, userMain);
                }
            }
        }
    }

    public void deleteFriends (long id, long friendId) {
        User userMain = inMemoryUserStorage.get(id);
        User userFriend = inMemoryUserStorage.get(friendId);

        if (userMain == null ) {
            throw new NotFoundException(String.format("User с id = %s не найден",id));
        } else {
            List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int)id);
            if (listUserFriends != null) {
                User userExist = listUserFriends.stream().filter(friendObjeck -> friendObjeck.getId() == friendId)
                        .findFirst()
                        .orElse(null);
                if (userExist != null) {
                    inMemoryUserStorage.deleteFriends((int) id, userFriend);
                }
            }
        }

        if (userFriend == null) {
            throw new NotFoundException(String.format("Friend с id = %s не найден",friendId));
        }else {
            List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int)friendId);
            if (listUserFriends != null) {
                User userExist = listUserFriends.stream().filter(friendObjeck -> friendObjeck.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (userExist != null) {
                    inMemoryUserStorage.deleteFriends((int) friendId, userMain);
                }
            }
        }
    }

    public Collection<User> getFriends(long id) {
        return inMemoryUserStorage.getFriends((int) id);
    }

    public Collection<User> getCommonFriends (@PathVariable long id,
                                  @PathVariable long otherId) {
        User userMain = inMemoryUserStorage.get(id);
        User userFriend = inMemoryUserStorage.get(otherId);

        if (userMain == null ) {
            throw new NotFoundException(String.format("User с id = %s не найден",id));
        }
        if (userFriend == null) {
            throw new NotFoundException(String.format("Friend с id = %s не найден",otherId));
        }
        List<User> listUser1 = inMemoryUserStorage.userFriends.get((int)id);
        List<User> listUser2 = inMemoryUserStorage.userFriends.get((int)otherId);

        if (listUser1 == null && listUser2 == null) {
            return new ArrayList<>();
        }

        Collection<User> commonFriends = listUser1.stream()
                .filter(listUser2::contains)
                .toList();
        return  commonFriends;
    }

    // вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = inMemoryUserStorage.userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
