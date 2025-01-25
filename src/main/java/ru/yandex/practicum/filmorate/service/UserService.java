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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserDbStorage userDbStorage;

    //private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        //this.inMemoryUserStorage = inMemoryUserStorage;
        this.userDbStorage = userDbStorage;
    }

    public Collection<User> findAll() {
        return userDbStorage.getAll();
        //return inMemoryUserStorage.getAll();
    }

    public Optional<User> getUserById(long id){
        return userDbStorage.get(id);
    }
    public User create(User user) throws ParseException {
        ValidationUtils.validateUser(user);

        //user.setId((int) getNextId());
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
//        if (inMemoryUserStorage.userMap.containsKey(user.getId())) {
//            return inMemoryUserStorage.update(user);
//        }
        throw new NotFoundException(String.format("User with id = %s not found", user.getId()));
    }

    public void addFriends(long id, long friendId) {
        //Optional<User> userMain = inMemoryUserStorage.get(id);
        //Optional<User> userFriend = inMemoryUserStorage.get(friendId);

        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        userDbStorage.addFriends((int) id,(int) friendId);
        //addFriendToList(id, friendId, userFriend.orElse(null));
        //addFriendToList(friendId, id, userMain.orElse(null));
    }

//    private void addFriendToList(long id, long friendId, User friend) {
//        List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int) id);
//        if (listUserFriends != null) {
//            boolean friendExists = listUserFriends.stream()
//                    .anyMatch(friendObj -> friendObj.getId() == friendId);
//            if (!friendExists) {
//                inMemoryUserStorage.addFriends((int) id, friend);
//            }
//        }
//    }

    public void deleteFriends(long id, long friendId) {
        //Optional<User> userMain = inMemoryUserStorage.get(id);
        //Optional<User> userFriend = inMemoryUserStorage.get(friendId);
        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }
        //deleteFriendFromList(id, friendId, userFriend.orElse(null));
        userDbStorage.deleteFriends((int) friendId,(int) id);

        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }
        //deleteFriendFromList(friendId, id, userMain.orElse(null));
        //userDbStorage.deleteFriends((int) friendId,(int) id);
    }

//    private void deleteFriendFromList(long id, long friendId, User friend) {
//        List<User> listUserFriends = inMemoryUserStorage.userFriends.get((int) id);
//        if (listUserFriends != null) {
//            boolean friendExists = listUserFriends.stream()
//                    .anyMatch(friendObj -> friendObj.getId() == friendId);
//            if (friendExists) {
//                inMemoryUserStorage.deleteFriends((int) id, friend);
//            }
//        }
//    }

    public Collection<User> getFriends(long id) {
        Optional<User> userMain = userDbStorage.get(id);
        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }
        Collection<User> listUserFriends = userDbStorage.getFriends(id);

        return listUserFriends;
    }

    public Collection<User> getCommonFriends(long id, long friendId) {
        //Optional<User> userMain = inMemoryUserStorage.get(id);
        //Optional<User> userFriend = inMemoryUserStorage.get(friendId);
        Optional<User> userMain = userDbStorage.get(id);
        Optional<User> userFriend = userDbStorage.get(friendId);

        if (userMain.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", id));
        }
        if (userFriend.isEmpty()) {
            throw new NotFoundException(String.format("User with id = %s not found", friendId));
        }

        return userDbStorage.getCommonFriends((int) id,(int) friendId);

//        List<User> listUser1 = inMemoryUserStorage.userFriends.get((int) id);
//        List<User> listUser2 = inMemoryUserStorage.userFriends.get((int) friendId);
//
//        if (listUser1 == null || listUser2 == null) {
//            return new ArrayList<>();
//        }
//
//        return listUser1.stream()
//                .filter(listUser2::contains)
//                .toList();
    }

//    private long getNextId() {
//        return inMemoryUserStorage.userMap.keySet().stream()
//                .mapToLong(id -> id)
//                .max()
//                .orElse(0) + 1;
//    }
}
