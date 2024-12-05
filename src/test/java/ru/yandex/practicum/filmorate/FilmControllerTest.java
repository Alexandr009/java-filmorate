package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmControllerTest {
    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void tearDown() {
      ///  taskServer.stop();
    }

    @Test
    public void checkValidationCondition(){
        Film newFilms = new Film();
        newFilms.setId(1);
        newFilms.setName("Test1");
        newFilms.setDescription("Description");
        //newFilms.setReleaseDate("2000-08-20");

    }

}
