package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Setter
@Getter
@ToString
public class User {

    @Positive(message = "id can be only positive")
    private Integer id;
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email is mandatory")
    private String email;

    @Pattern(regexp = "^\\S*$", message = "В логине не может содержаться пробел!")
    @NotBlank(message = "login cannot be null or empty") // NotBlank сам проверяет на null
    private String login;


    @NotNull(message = "Birthday cannot be null")
    @PastOrPresent(message = "You cannot burn in future")
    private LocalDate birthday;


    public User(String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(this.email, user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
