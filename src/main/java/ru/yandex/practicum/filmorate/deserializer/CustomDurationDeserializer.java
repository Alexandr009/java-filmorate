package ru.yandex.practicum.filmorate.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class CustomDurationDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String durationString = p.getText();
        //return Duration.parse(durationString); // Преобразует строку в объект Duration
        //long minutes = p.getLongValue(); // Получаем значение как число минут
        return Duration.ofMinutes(Long.parseLong(durationString)); // Преобразуем в Duration
        //return Duration.ofMinutes(minutes); // Преобразуем в Duration
    }
}
