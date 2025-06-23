package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.validation.annotation.ReleaseDate;

import java.time.LocalDate;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class Film {
    int id;
    @NotBlank(message = "Название не может быть пустым.")
    String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    String description;
    Set<String> genre;
    int contentRatingId;
    @ReleaseDate
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    int duration; // указываем в минутах.
}
