package ru.yandex.practicum.filmorate.repository.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void confirmFriendship(int userId, int friendId);

    void deleteAll();

    boolean existsFriendship(int userId, int friendId);

    boolean existsById(int userId);

    List<User> findAll();

    Optional<User> findByEmail(String email);

    Optional<User> findById(int id);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId1, int userId2);

    boolean isValidFriendRequest(int userId, int friendId);

    void makeFriends(int userId, int friendId, boolean isConfirmed);

    void removeFriends(int userId, int friendId);

    User save(User user);
}
