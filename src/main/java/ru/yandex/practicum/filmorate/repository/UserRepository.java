package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = new UserMapper();

    public void confirmFriendship(int userId, int friendId) {
        String sql = "UPDATE friends SET `is_confirmed` = ? WHERE `user_id` = ? AND `friend_id` = ?";
        jdbcTemplate.update(sql, true, userId, friendId);
        jdbcTemplate.update(sql, true, friendId, userId);
    }

    public void deleteAll() {
        String sql = """
            DELETE FROM friends;
            DELETE FROM users
            """;
        jdbcTemplate.batchUpdate(sql);
    }

    public boolean existsFriendship(int userId, int friendId) {
        String sql = """
                SELECT COUNT(*) FROM friends WHERE (user1_id = ? AND user2_id = ?) OR (user2_id = ? AND user1_id = ?)
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId, userId, friendId);
        return count != null && count > 0;
    }

    public boolean existsById(int userId) {
        String sql = "SELECT COUNT(1) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, email);
        return results.stream().findFirst();
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, userRowMapper, id);
        return results.stream().findFirst();
    }

    public List<User> getFriends(int userId) {
        String sql = """
        SELECT u.*
        FROM friends f
        JOIN users u ON (f.user1_id = ? AND u.id = f.user2_id) OR (f.user2_id = ? AND u.id = f.user1_id)
        """;

        return jdbcTemplate.query(sql, userRowMapper, userId, userId);
    }

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

    public boolean isValidFriendRequest(int userId, int friendId) {
        // Проверяем, что именно friendId отправлял запрос
        String sql = "SELECT user1_id FROM friends WHERE user1_id = ? AND user2_id = ?";
        List<Integer> results = jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getInt("user1_id"),
                friendId, userId
        );
        return !results.isEmpty();
    }

    public void makeFriends(int userId, int friendId) {
        String sql = "INSERT INTO friends (user1_id, user2_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public void removeFriends(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE (user1_id = ? AND user2_id = ?) OR (user2_id = ? AND user1_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    /**
     * Метод сохранения пользователя.
     * Если id пользователя больше нуля, значит пользователь присутствует в базе. Вызываем метод обновления.
     * Если id == 0, добавляем пользователя в базу.
     */
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

