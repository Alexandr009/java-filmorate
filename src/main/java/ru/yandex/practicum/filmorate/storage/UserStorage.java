package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public void creat(User user);
    public User get(long id);
    public Collection<User> getAll();
    public User update(User user);
    public void addFriends (Integer userId, User friend);
    public void deleteFriends (Integer userId, User friend);
    public Collection<User> getFriends(long id);
}
