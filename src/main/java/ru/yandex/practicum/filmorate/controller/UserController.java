package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll(){
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) throws ParseException {
        validationJson(user);
        user.setId((int) getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user){
        if (user.getId().toString() == "" || user.getId() == null){
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())){
            User userOld = users.get(user.getId());
            userOld.setEmail(user.getEmail());
            userOld.setLogin(user.getLogin());
            userOld.setName(user.getName());
            userOld.setBirthday(user.getBirthday());
            return userOld;
        }
        throw new NotFoundException("User с id = " + user.getId() + " не найден");
    }

    //проверка входящего запроса
    public void validationJson(User user) throws ParseException {
        LocalDate currentLocalDate = LocalDate.now();

        // Преобразование Date в LocalDate
        Date birthdayDate = user.getBirthday();
        LocalDate birthLocalDate = birthdayDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if(user.getEmail().isBlank() || !user.getEmail().contains("@")){
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        } else if (birthLocalDate.isAfter(currentLocalDate)) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
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
