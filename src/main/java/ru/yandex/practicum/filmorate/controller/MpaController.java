package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.interfaces.MpaService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> findAllMpa() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findMpaById(@PathVariable int id) {
        return mpaService.findById(id);
    }
}
