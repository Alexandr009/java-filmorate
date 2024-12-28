package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.text.ParseException;
import java.util.Collection;

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
    public User update(@RequestBody User user) throws ParseException {
        log.info("update user started - " + String.valueOf(user));
        User userNew = userService.update(user);
        log.info("update user finished - " + String.valueOf(userNew));
        return userNew;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriends (@PathVariable long id,
            @PathVariable long friendId) throws ParseException {
        log.info("addFriends started: users id - " + id + "friend id -" + friendId);
        userService.addFriends(id, friendId);
        log.info("addFriends finished: users id - " + id + "friend id -" + friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriends (@PathVariable long id,
                              @PathVariable long friendId) {
        log.info("deleteFriends started: users id - " + id + "friend id -" + friendId);
        userService.deleteFriends(id, friendId);
        log.info("deleteFriends finished: users id - " + id + "friend id -" + friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable long id) {
        log.info("findFriends started: users id - " + id);
        Collection<User> friends = userService.getFriends(id);
        log.info("findFriends finished: " + friends.toString());
        return friends;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends (@PathVariable long id,
                               @PathVariable long otherId) {
        log.info("getCommonFriends started: users id - " + id + "otherId id -" + otherId);
        Collection<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("getCommonFriends finished: " + commonFriends.toString());
        return commonFriends;
    }
}

