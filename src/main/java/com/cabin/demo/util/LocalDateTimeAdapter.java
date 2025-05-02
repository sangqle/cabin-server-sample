package com.cabin.demo.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter
        implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public JsonElement serialize(
            LocalDateTime src,
            Type typeOfSrc,
            JsonSerializationContext context
    ) {
        // Serialize to ISO-8601 string, e.g. "2025-05-02T14:30:00"
        return new JsonPrimitive(src.format(FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
    ) throws JsonParseException {
        // Parse the ISO-8601 string back to LocalDateTime
        return LocalDateTime.parse(json.getAsString(), FORMATTER);
    }
}