package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.deserializer.CustomDateDeserializer;
import ru.yandex.practicum.filmorate.deserializer.CustomDateSerializer;
import ru.yandex.practicum.filmorate.deserializer.CustomDurationDeserializer;
import ru.yandex.practicum.filmorate.deserializer.CustomDurationSerializer;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
public class Film {
    Integer id;
    String name;
    String description;

    @JsonDeserialize(using = CustomDurationDeserializer.class)
    @JsonSerialize(using = CustomDurationSerializer.class)
    Duration duration;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    Date releaseDate;
    Integer rating;
    Mpa mpa;
    List<Genre> genres;
}
