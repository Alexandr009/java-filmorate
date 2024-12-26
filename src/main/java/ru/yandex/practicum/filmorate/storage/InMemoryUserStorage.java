package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    public HashMap <Integer, User> userMap = new HashMap<>();
    HashMap <Integer, List<User>> userFriends = new HashMap<>();

    @Override
    public void creat(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public Collection<User> get() {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    @Override
    public User update(User user) {
            User userOld = userMap.get(user.getId());
            userOld.setEmail(user.getEmail());
            userOld.setLogin(user.getLogin());
            userOld.setName(user.getName());
            userOld.setBirthday(user.getBirthday());
            return userOld;
    }
}
