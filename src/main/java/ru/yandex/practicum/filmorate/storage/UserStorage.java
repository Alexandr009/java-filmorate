package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public void creat(User user);
    public Collection<User> get();
    public Collection<User> getAll();
    public User update(User user);
}
