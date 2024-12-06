package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.deserializer.CustomDateDeserializer;
import ru.yandex.practicum.filmorate.deserializer.CustomDateSerializer;

import java.util.Date;

@Getter
@Setter
@Data
public class User {
    Integer id;
    String email;
    String login;
    String name;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    Date birthday;
}
