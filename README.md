https://drawsql.app/none-1054/diagrams/filmorate
Ссылка на диаграмму
![drawSQL-export-2022-08-04_16_10](https://user-images.githubusercontent.com/92802270/182843874-90767848-bc8f-4d7d-9ae4-4f2ecb7f4570.png)

```{Java}< >{
SELECT first
FROM friends
WHERE first = user_id AND confirmed IS TRUE
UNION
SELECT second
FROM friends
WHERE second = user_id AND confirmed IS TRUE}```
