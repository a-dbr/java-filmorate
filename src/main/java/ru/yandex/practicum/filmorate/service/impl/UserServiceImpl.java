package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OperationNotAllowedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.interfaces.UserRepository;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void confirmFriendship(int userId, int friendId) {
        validateUsersExist(userId, friendId);

        // проверяем, что запрос на дружбу был от friendId к userId
        if (!userRepository.isValidFriendRequest(friendId, userId)) {
            throw new NotFoundException("Не найден запрос от указанного пользователя: " + friendId);
        }

        userRepository.confirmFriendship(userId, friendId);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (user.getId() != 0) {
            throw new InvalidJsonFieldException("ID не должен быть указан при создании пользователя.");
        }

        User finalUser = user;
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    throw new EmailAlreadyExistsException(finalUser.getEmail());
                });

        // Если у пользователя не указано имя, присваиваем полю name значение поля login
        if (user.getName() == null || user.getName().isBlank()) {
            user = user.toBuilder().name(user.getLogin()).build();
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        validateUsersExist(userId, otherUserId);
        return userRepository.getCommonFriends(userId, otherUserId);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден."));
    }

    @Override
    public List<User> getUserFriends(int id) {
        validateUserExists(id);
        return userRepository.getFriends(id);
    }

    @Override
    @Transactional
    public void makeFriends(int userId, int friendId) {
        validateBasicRequest(userId, friendId);

        if (userRepository.existsFriendship(userId, friendId)) {
            throw new OperationNotAllowedException("Пользователи уже являются друзьями.");
        }
        // проверяем на повторный запрос
        if (userRepository.isValidFriendRequest(userId, friendId)) {
            throw new OperationNotAllowedException("Нельзя отправить запрос на добавление в друзья дважды.");
        }
        // проверяем, есть ли неподтвержденный запрос от friendId к userId
        if (userRepository.isValidFriendRequest(friendId, userId)) {
            userRepository.confirmFriendship(userId, friendId);
        } else if (!userRepository.isValidFriendRequest(userId, friendId)) { // Если запроса нет - создаем новый
            userRepository.makeFriends(userId, friendId, false);
        }
    }

    @Override
    @Transactional
    public void removeFriend(int userId, int friendId) {
        validateBasicRequest(userId, friendId);
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

    // вспомогательные методы валидации
    private void validateUserExists(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден.");
        }
    }

    private void validateUsersExist(int userId1, int userId2) {
        validateUserExists(userId1);
        validateUserExists(userId2);
    }

    private void validateBasicRequest(int userId, int friendId) {
        if (userId < 1 || friendId < 1) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом.");
        }
        if (userId == friendId) {
            throw new OperationNotAllowedException("ID пользователей должны отличаться.");
        }
        validateUsersExist(userId, friendId);
    }
}
