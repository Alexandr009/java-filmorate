package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

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
        log.info(String.format("findAll user started - %s",userService.findAll().toString()));
        Collection<User> user = userService.findAll();
        log.info(String.format("findAll user finished - %s",user.toString()));
        return user;
    }

    @GetMapping("/{id}")
    public Optional<Optional<User>> getUserById(@PathVariable long id) {
        log.info(String.format("getUserById started - %s - ", id));
        return Optional.ofNullable(userService.getUserById(id));
    }

    @PostMapping
    public User create(@RequestBody User user) throws ParseException {
        log.info(String.format("create user started - %s",String.valueOf(user)));
        User userNew = userService.create(user);
        log.info(String.format("create user finished - %s",userNew.toString()));
        return userNew;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ParseException {
        log.info(String.format("update user started - %s",String.valueOf(user)));
        User userNew = userService.update(user);
        log.info(String.format("update user finished - %s",String.valueOf(userNew)));
        return userNew;
    }

    @PutMapping("/{id}/friends/{friend-id}")
    public void addFriends(@PathVariable long id,
                           @PathVariable("friend-id") long friendId) throws ParseException {
        log.info(String.format("addFriends started: users id - %s, friend id - %s",id,friendId));
        userService.addFriends(id, friendId);
        log.info(String.format("addFriends finished: users id - %s, friend id - %s",id,friendId));
    }

    @DeleteMapping("{id}/friends/{friend-id}")
    public void deleteFriends(@PathVariable long id,
                              @PathVariable("friend-id") long friendId) {
        log.info(String.format("deleteFriends started: users id - %s, friend id - %s",id,friendId));
        userService.deleteFriends(id, friendId);
        log.info(String.format("deleteFriends finished: users id - %s, friend id - %s",id,friendId));
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable long id) {
        log.info(String.format("findFriends started: users id - %s",id));
        Collection<User> friends = userService.getFriends(id);
        log.info(String.format("findFriends finished: %s",friends.toString()));
        return friends;
    }

    @GetMapping("{id}/friends/common/{friend-id}")
    public Collection<User> getCommonFriends(@PathVariable long id,
                                             @PathVariable("friend-id") long friendId) {
        log.info(String.format("getCommonFriends started: users id - %s, otherId id - %s",id,friendId));
        Collection<User> commonFriends = userService.getCommonFriends(id, friendId);
        log.info(String.format("getCommonFriends finished: %s",commonFriends.toString()));
        return commonFriends;
    }
}


