package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Setter
@Getter
@Builder
@NoArgsConstructor
public class User {
    private Long id;

    private String name;

    @Pattern(regexp = "^\\S+$", message = "В логине не может содержаться пробел!")
    @NotNull(message = "login cannot be null") // NotBlank сам проверяет на null
    private String login;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email is mandatory")
    private String email;

    @NotNull(message = "Birthday cannot be null")
    @PastOrPresent(message = "You cannot burn in future")
    private LocalDate birthday;

    public User(String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = name;
    }

    public User(long id, String name, String login, String email, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
    }


    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("email", email);
        values.put("login", login);
        values.put("birthday", birthday);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
