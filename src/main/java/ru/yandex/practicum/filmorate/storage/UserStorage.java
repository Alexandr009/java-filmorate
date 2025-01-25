package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    public User creat(User user);

    public Optional<User> get(long id);

    public Collection<User> getAll();

    public User update(User user);

    public void addFriends(Integer userId, Integer friendId);

    public void deleteFriends(Integer userId, Integer friendId);

    public Collection<User> getFriends(long id);
}
