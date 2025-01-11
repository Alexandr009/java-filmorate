package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Frends {
    Integer id;
    Integer userId;
    Integer frendId;
}
