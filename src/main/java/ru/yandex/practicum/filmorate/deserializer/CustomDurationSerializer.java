package ru.yandex.practicum.filmorate.deserializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

public class CustomDurationSerializer extends StdSerializer<Duration> {

    public CustomDurationSerializer() {
        super(Duration.class);
    }

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        long minutes = value.toMinutes(); // Преобразуем в минуты
        gen.writeNumber(minutes); // Выводим в JSON как число
    }
}
