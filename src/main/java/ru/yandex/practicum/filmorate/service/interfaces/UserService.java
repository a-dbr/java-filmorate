package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    List<User> getAllUsers();

    List<Integer> getCommonFriends(int userId, int otherUserId);

    User getUserById(int id);

    List<Integer> getUserFriends(int id);

    void makeFriends(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    User updateUser(User user);
}
