-- Добавление значений в таблицу mpa
MERGE INTO mpa (id, name) VALUES (1, 'G');
MERGE INTO mpa (id, name) VALUES (2, 'PG');
MERGE INTO mpa (id, name) VALUES (3, 'PG-13');
MERGE INTO mpa (id, name) VALUES (4, 'R');
MERGE INTO mpa (id, name) VALUES (5, 'NC-17');

-- Добавление значений в таблицу genres
MERGE INTO genres (id, name) VALUES (1, 'Комедия');
MERGE INTO genres (id, name) VALUES (2, 'Драма');
MERGE INTO genres (id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (id, name) VALUES (4, 'Триллер');
MERGE INTO genres (id, name) VALUES (5, 'Документальный');
MERGE INTO genres (id, name) VALUES (6, 'Боевик');

-- Добавление значений в таблицу friendship_status
MERGE INTO FRIENDS_STATUS (id, status) VALUES (1, 'Подтверждена');
MERGE INTO FRIENDS_STATUS (id, status) VALUES (2, 'На рассмотрении');