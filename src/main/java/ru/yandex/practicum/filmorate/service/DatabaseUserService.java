package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DatabaseUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (user.getId() != 0) {
            throw new InvalidJsonFieldException("ID не должен быть указан при создании пользователя.");
        }

        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        // Если у пользователя не указано имя, присваиваем полю name значение поля login
        if (user.getName() == null || user.getName().isBlank()) {
            user = user.toBuilder()
                    .name(user.getLogin())
                    .build();
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.findById(user.getId())
                .map(existingUser -> userRepository.save(user))
                .orElseThrow(() -> new NotFoundException(
                        "Обновление невозможно. Пользователь с ID " + user.getId() + " не найден."));
    }
}
