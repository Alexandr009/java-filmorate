package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("findAll user started - " + userService.findAll().toString());
        Collection<User> user = userService.findAll();//users.values();
        log.info("findAll user finished - " +user.toString());
        return user;
    }

    @PostMapping
    public User create(@RequestBody User user) throws ParseException {
        log.info("create user started - " + String.valueOf(user));
        User userNew = userService.create(user);
        log.info("create user finished - " + userNew.toString());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.info("update user started - " + String.valueOf(user));
        User userNew = userService.update(user);
        log.info("update user finished - " + String.valueOf(userNew));
        return userNew;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriends (@PathVariable long id,
            @PathVariable long friendId) {
        log.info("users id - " + id + "friend id -" + friendId);
    }

}
