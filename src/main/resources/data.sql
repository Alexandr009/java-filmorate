MERGE INTO mpa (id, name)
VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

MERGE INTO genre (id, name) VALUES (1, 'Комедия');
MERGE INTO genre (id, name) VALUES (2, 'Драма');
MERGE INTO genre (id, name) VALUES (3, 'Мультфильм');
MERGE INTO genre (id, name) VALUES (4, 'Триллер');
MERGE INTO genre (id, name) VALUES (5, 'Документальный');
MERGE INTO genre (id, name) VALUES (6, 'Боевик');