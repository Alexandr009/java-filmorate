package ru.yandex.practicum.filmorate.validation;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
@Slf4j
public class ValidationUtils {

    // Метод для валидации Film
    public static void validateFilm(Film film) throws ParseException {
        String dateString = "1895-12-28";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date earliestReleaseDate = format.parse(dateString);

        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().before(earliestReleaseDate)) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() != null && film.getDuration().isNegative()) {
            log.error("Продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }

    // Метод для валидации User
    public static void validateUser(User user) {
        LocalDate currentLocalDate = LocalDate.now();

        // Преобразование Date в LocalDate
        Date birthdayDate = user.getBirthday();
        LocalDate birthLocalDate = birthdayDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (birthLocalDate.isAfter(currentLocalDate)) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
