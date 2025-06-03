CREATE TABLE users
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    email    VARCHAR(255) NOT NULL,
    login    VARCHAR(100) NOT NULL,
    name     VARCHAR(255),
    birthday DATE
);

CREATE TABLE friends
(
    user_id   INT,
    friend_id INT,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE films
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    release_date DATE,
    duration     INT          NOT NULL
);

CREATE TABLE likes
(
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);