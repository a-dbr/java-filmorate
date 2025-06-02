package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OperationNotAllowedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<User> getCommonFriends(int userId, int otherUserId) {
        if (userRepository.findById(userId).isEmpty() ||
                userRepository.findById(otherUserId).isEmpty()) {
            throw new NotFoundException("Невозможно получить общих друзей: один из пользователей не найден");
        }

        List<User> userFriends = userRepository.getFriends(userId);
        List<User> otherFriends = userRepository.getFriends(otherUserId);

        Set<User> otherSet = new HashSet<>(otherFriends);
        List<User> common = userFriends.stream()
                .filter(otherSet::contains)
                .collect(Collectors.toList());

        if (common.isEmpty()) {
            throw new NotFoundException("У пользователей нет общих друзей");
        }

        return common;
    }

    @Override
    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }
    }

    @Override
    public List<User> getUserFriends(int id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }
        return userRepository.getFriends(id);
    }

    @Override
    @Transactional
    public void makeFriends(int userId, int friendId) {
        if (userId < 1 || friendId < 1) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом.");
        }
        if (userId == friendId) {
            throw new OperationNotAllowedException("ID пользователей должны отличаться.");
        }
        if (!userRepository.existsById(userId) || !userRepository.existsById(friendId)) {
            throw new NotFoundException("Невозможно добавить в друзья: один из пользователей не найден.");
        }
        if (userRepository.existsFriendship(userId, friendId)) {
            throw new OperationNotAllowedException("Пользователи уже являются друзьями.");
        }
        userRepository.makeFriends(userId, friendId);
    }

    @Override
    @Transactional
    public void removeFriend(int userId, int friendId) {
        if (userId < 1 || friendId < 1) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом.");
        }
        if (userId == friendId) {
            throw new OperationNotAllowedException("ID пользователей должны отличаться.");
        }
        if (!userRepository.existsById(userId) || !userRepository.existsById(friendId)) {
            throw new NotFoundException("Невозможно удалить дружбу: один из пользователей не найден.");
        }
        userRepository.removeFriends(userId, friendId);
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
