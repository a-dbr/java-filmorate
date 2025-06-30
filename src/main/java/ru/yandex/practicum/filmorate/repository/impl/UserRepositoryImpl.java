package ru.yandex.practicum.filmorate.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.interfaces.UserRepository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper;

    @Override
    public void confirmFriendship(int confirmingUserId, int friendId) {
        String sql = "UPDATE friends SET is_confirmed = true WHERE user1_id = ? AND user2_id = ?";
        jdbcTemplate.update(sql, friendId, confirmingUserId);
    }

    @Override
    public void deleteAll() {
        String sql = """
                DELETE FROM friends;
                DELETE FROM users
                """;
        jdbcTemplate.batchUpdate(sql);
    }

    @Override
    public boolean existsFriendship(int userId, int friendId) {
        String sql = """
                SELECT COUNT(*)
                FROM friends
                WHERE (user1_id = ? AND user2_id = ? AND is_confirmed = ?)
                   OR (user2_id = ? AND user1_id = ? AND is_confirmed = ?)
                """;
        // Для существующей дружбы оба направления должны быть подтверждены
        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                userId, friendId, true,
                userId, friendId, true
        );
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(int userId) {
        String sql = "SELECT COUNT(1) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, email);
        return results.stream().findFirst();
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, id);
        return results.stream().findFirst();
    }

    // Если user1_id добавил в друзья user2_id и выгружаем для него, то добавляем в список
    // Если user1_id добавил в друзья, но выгружаем друзей user2_id, то сначала проверяем подтверждение
    @Override
    public List<User> getFriends(int userId) {
        String sql = """
                SELECT u.*
                FROM users u
                WHERE u.id IN (
                SELECT f.user2_id FROM friends f WHERE f.user1_id = ?
                UNION
                SELECT f.user1_id FROM friends f WHERE f.user2_id = ? AND f.is_confirmed = true
                )
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId1, int userId2) {
        String sql = """
                SELECT u.* FROM users u
                JOIN friends f1 ON (
                    f1.user1_id = ? AND u.id = f1.user2_id) OR (f1.user2_id = ? AND u.id = f1.user1_id)
                JOIN friends f2 ON
                    (f2.user1_id = ? AND u.id = f2.user2_id) OR (f2.user2_id = ? AND u.id = f2.user1_id)
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId1, userId2, userId2, userId1);
    }

    @Override
    public boolean isValidFriendRequest(int fromUserId, int toUserId) {
        String sql = "SELECT COUNT(*) FROM friends WHERE user1_id = ? AND user2_id = ? AND is_confirmed = false";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, fromUserId, toUserId);
        return count != null && count > 0;
    }

    @Override
    public void makeFriends(int userId, int friendId, boolean isConfirmed) {
        String sql = "INSERT INTO friends (user1_id, user2_id, is_confirmed) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, isConfirmed);
    }

    @Override
    public void removeFriends(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE (user1_id = ? AND user2_id = ?) OR (user2_id = ? AND user1_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    /**
     * Метод сохранения пользователя.
     * Если id пользователя больше нуля, значит пользователь присутствует в базе. Вызываем метод обновления.
     * Если id == 0, добавляем пользователя в базу.
     */
    @Override
    public User save(User user) {
        if (user.getId() > 0) {
            update(user);
            return user;
        } else {
            return insert(user);
        }
    }

    private User insert(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);
        // используем KeyHolder для присвоения пользователю id и возвращаем обновленный объект.
        return user.toBuilder().id(keyHolder.getKey().intValue()).build();
    }

    private void update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
    }
}

