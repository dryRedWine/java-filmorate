https://drawsql.app/none-1054/diagrams/filmorate
Ссылка на диаграмму
![drawSQL-export-2022-08-04_16_10](https://user-images.githubusercontent.com/92802270/182843874-90767848-bc8f-4d7d-9ae4-4f2ecb7f4570.png)

  
  
Примерный запрос:  
```{Java} {
SELECT user1_id
FROM friendship
WHERE user1_id = user_id 
AND status_id = 1
UNION
SELECT user2_id
FROM friendship
WHERE user2_id = user_id 
AND status_id = 1}
```
