package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;

@Service
public class UserService {
    //не логируют
    //добавление в друзья, удаление из друзей, вывод списка общих друзей.
    //Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.

    //private final Map<Integer, User> users = new HashMap<>();
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

    public User update(User user) {
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
