package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.ValidationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ValidationUtilsTest {

    @Test
    void validateFilm_ValidFilm_ShouldPass() throws ParseException {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid Description");
        film.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"));
        film.setDuration(java.time.Duration.ofMinutes(120));

        Assertions.assertDoesNotThrow(() -> ValidationUtils.validateFilm(film));
    }

    @Test
    void validateFilm_EmptyName_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid Description");
        film.setReleaseDate(new Date());
        film.setDuration(java.time.Duration.ofMinutes(120));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> ValidationUtils.validateFilm(film));
        Assertions.assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    void validateFilm_TooLongDescription_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(new Date());
        film.setDuration(java.time.Duration.ofMinutes(120));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> ValidationUtils.validateFilm(film));
        Assertions.assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void validateFilm_InvalidReleaseDate_ShouldThrowValidationException() throws ParseException {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid Description");
        film.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").parse("1800-01-01"));
        film.setDuration(java.time.Duration.ofMinutes(120));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> ValidationUtils.validateFilm(film));
        Assertions.assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void validateFilm_NegativeDuration_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid Description");
        film.setReleaseDate(new Date());
        film.setDuration(java.time.Duration.ofMinutes(-10));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> ValidationUtils.validateFilm(film));
        Assertions.assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }

    @Test
    void validateUser_ValidUser_ShouldPass() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("ValidLogin");
        user.setBirthday(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 18)); // 18 лет назад

        Assertions.assertDoesNotThrow(() -> ValidationUtils.validateUser(user));
    }

    @Test
    void validateUser_InvalidEmail_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("ValidLogin");
        user.setBirthday(new Date());

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> ValidationUtils.validateUser(user));
        Assertions.assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void validateUser_LoginWithSpaces_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("Invalid Login");
        user.setBirthday(new Date());

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> ValidationUtils.validateUser(user));
        Assertions.assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void validateUser_BirthDateInFuture_ShouldThrowValidationException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("ValidLogin");
        user.setBirthday(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)); // Завтра

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> ValidationUtils.validateUser(user));
        Assertions.assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}