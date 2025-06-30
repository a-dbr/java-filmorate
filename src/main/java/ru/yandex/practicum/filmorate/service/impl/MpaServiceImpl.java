package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.interfaces.MpaRepository;
import ru.yandex.practicum.filmorate.service.interfaces.MpaService;

import java.util.List;

@Service
@AllArgsConstructor
public class MpaServiceImpl implements MpaService {
    MpaRepository mpaRepository;

    @Override
    public List<Mpa> findAll() {
        return mpaRepository.findAll();
    }

    @Override
    public Mpa findById(int id) {
        return mpaRepository.findById(id).orElseThrow(() -> new NotFoundException("Рейтинг с таким ID не найден."));
    }
}
