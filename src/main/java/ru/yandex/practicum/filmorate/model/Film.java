package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.validation.annotation.ReleaseDate;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class Film {
    int id;
    @NotBlank(message = "Название не может быть пустым.")
    String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    String description;
    @ReleaseDate
    LocalDate releaseDate; //вопрос: использовать LocalDate или лучше Date при работе с jdbc?
    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    int duration; // указываем в минутах.
}
