package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    public HashMap<Integer, User> userMap = new HashMap<>();
    public HashMap<Integer, List<User>> userFriends = new HashMap<>();

    @Override
    public void creat(User user) {
        userMap.put(user.getId(), user);
        userFriends.putIfAbsent(user.getId(), new ArrayList<>());
    }

    @Override
    public User get(long id) {
        User user = userMap.get((int) id);
        return user;
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

    @Override
    public void addFriends(Integer userId, User friend) {
        userFriends.get(userId).add(friend);
    }

    @Override
    public void deleteFriends(Integer userId, User friend) {
        userFriends.get(userId).remove(friend);
    }

    @Override
    public Collection<User> getFriends(long id) {
        Collection<User> users = userFriends.get((int)id);
        return users;
    }

}
