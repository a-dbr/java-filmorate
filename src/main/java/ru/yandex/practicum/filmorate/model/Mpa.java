package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Value
@Builder(toBuilder = true)
public class Mpa {
    int id;
    String name;
}
