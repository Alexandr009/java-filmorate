# ?java-filmorate

Filmorate project.

1. shema picture in directory "java-filmorate\ER-diagram.png"

![ERdiagram.png](assets/ER-diagram.png?t=1736797464006)

2. table relationship: genre - film (N to N) relationship table genre_film, mpa - film (1 to N), film - user (N to N) relationship table filml_ikes, user - frend(user) (N to N) relationship table frends.
3. Main request:

   User create - /users

   ```
   {
     "login": "dolore",
     "name": "Nick Name",
     "email": "mail@mail.ru",
     "birthday": "1946-08-20"
   }
   ```
   Friend add - /users/{{id}}/friends/{{friend\_id}}

   Film create - /films

   ```
   {
     "name": "nisi eiusmod",
     "description": "adipisicing",
     "releaseDate": "1967-03-25",
     "duration": 100
   }
   ```
   Add like - /films/{{film\_id}}/like/{{id}}
