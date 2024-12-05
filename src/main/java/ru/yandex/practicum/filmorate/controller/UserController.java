package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info(users.values().toString());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) throws ParseException {
        log.info(String.valueOf(user));
        ValidationUtils.validateUser(user);

        user.setId((int) getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info(String.valueOf(user));
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        ValidationUtils.validateUser(user);

        if (user.getId() == null || user.getId().toString().isBlank()) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            User userOld = users.get(user.getId());
            userOld.setEmail(user.getEmail());
            userOld.setLogin(user.getLogin());
            userOld.setName(user.getName());
            userOld.setBirthday(user.getBirthday());
            log.info(String.valueOf(userOld));
            return userOld;
        }
        throw new NotFoundException("User с id = " + user.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
