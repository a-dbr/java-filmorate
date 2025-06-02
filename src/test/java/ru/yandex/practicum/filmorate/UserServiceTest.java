package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InvalidJsonFieldException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.OperationNotAllowedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FilmorateApplication.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void createUser() {
        User input = User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru")
                .login("r-kadiy")
                .name("Arkadiy")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User created = userService.createUser(input);
        assertNotEquals(0, created.getId());

        User fromDb = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("r-kadiy@yandex.ru", fromDb.getEmail());
        assertEquals("r-kadiy", fromDb.getLogin());
    }

    @Test
    void createUserWithId() {
        User bad = User.builder()
                .id(42)
                .email("r-kadiy@yandex.ru")
                .login("r-kadiy")
                .name("Arkadiy")
                .build();

        assertThrows(InvalidJsonFieldException.class,
                () -> userService.createUser(bad));
        assertTrue(userRepository.findAll().isEmpty(), "Создан пользователь с ID отличным от нуля.");
    }

    @Test
    void getAllUsers() {
        userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("u1").name("U1")
                .build());
        userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@ukupnik.ru").login("u2").name("U2")
                .build());

        List<User> all = userService.getAllUsers();
        assertEquals(2, all.size());
    }

    @Test
    void makeFriends() {
        User user1 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("u1").name("U1")
                .build());
        User user2 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@ukupnik.ru").login("u2").name("U2")
                .build());

        userService.makeFriends(user1.getId(), user2.getId());
        List<Integer> firstUserFriends = userService.getUserFriends(user1.getId());
        List<Integer> secondUserFriends = userService.getUserFriends(user2.getId());

        assertEquals(firstUserFriends.size(), secondUserFriends.size());
        assertTrue(firstUserFriends.contains(user2.getId()));
        assertTrue(secondUserFriends.contains(user1.getId()));
    }

    @Test
    void makeFriendsWithWrongId() {
        User user = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("u1").name("U1")
                .build());

        assertThrows(NotFoundException.class,
                () -> userService.makeFriends(user.getId(), 9999));
        assertThrows(IllegalArgumentException.class,
                () -> userService.makeFriends(-1, 0));
        assertThrows(OperationNotAllowedException.class,
                () -> userService.makeFriends(9999, 9999));
    }

    @Test
    void makeFriendsTwice() {
        User user1 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("u1").name("U1")
                .build());
        User user2 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@ukupnik.ru").login("u2").name("U2")
                .build());
        userService.makeFriends(user1.getId(), user2.getId());
        assertThrows(OperationNotAllowedException.class,
                () -> userService.makeFriends(user1.getId(), user2.getId()));
    }

    @Test
    void deleteFriend() {
        User user1 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("u1").name("U1")
                .build());
        User user2 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@ukupnik.ru").login("u2").name("U2")
                .build());

        userService.makeFriends(user1.getId(), user2.getId());
        userService.removeFriend(user1.getId(), user2.getId());
        assertThrows(NotFoundException.class,
                () -> userService.getUserFriends(user1.getId()));
        assertThrows(NotFoundException.class,
                () -> userService.getUserFriends(user2.getId()));
    }

    @Test
    void deleteFriendWithWrongId() {
        User user = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("u1").name("U1")
                .build());

        assertThrows(NotFoundException.class,
                () -> userService.removeFriend(user.getId(), 9999));
        assertThrows(IllegalArgumentException.class,
                () -> userService.removeFriend(-1, 0));
        assertThrows(OperationNotAllowedException.class,
                () -> userService.removeFriend(9999, 9999));
    }

    @Test
    void deleteFriendTwice() {
        User user1 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("u1").name("U1")
                .build());
        User user2 = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@ukupnik.ru").login("u2").name("U2")
                .build());
        userService.makeFriends(user1.getId(), user2.getId());
        userService.removeFriend(user1.getId(), user2.getId());
        assertThrows(OperationNotAllowedException.class,
                () -> userService.removeFriend(user1.getId(), user2.getId())
        );
    }

    @Test
    void updateUser() {
        User orig = userService.createUser(User.builder()
                .id(0)
                .email("r-kadiy@yandex.ru").login("r-kadiy").name("")
                .build());
        User modified = orig.toBuilder().name("Arkadiy").build();

        User updated = userService.updateUser(modified);
        assertEquals("Arkadiy", updated.getName());

        User fromDb = userRepository.findById(updated.getId()).orElseThrow();
        assertEquals("Arkadiy", fromDb.getName());
    }

    @Test
    void updateNonExistingUser() {
        User non = User.builder()
                .id(9999)
                .email("r-kadiy@ukupnik.ru")
                .login("u-kupnik").name("r-kadiy")
                .build();

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(non));
    }

    @Test
    void createUserWithExistingEmail() {
        User first = User.builder()
                .email("r-kadiy@ukupnik.ru")
                .login("u-kupnik")
                .build();

        User second = User.builder()
                .email("r-kadiy@ukupnik.ru")
                .login("r-kadiy")
                .build();

        userService.createUser(first);
        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(second));
    }
}

