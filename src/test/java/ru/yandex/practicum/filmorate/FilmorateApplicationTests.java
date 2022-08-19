package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {
	private final UserStorage userStorage;
	private final UserService userService;
	private final FilmStorage filmDBStorage;

	@Test
	@Order(1)
	public void testFindUserById() {

		Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	@Order(2)
	public void testGetAllUsers() {

		Optional<List<User>> userOptional = Optional.ofNullable(userStorage.findAll());

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user -> {
							assertThat(user.size() == 4).isTrue();
						}
				);
	}

//    @Test
//    public void testDeleteUserById() {
//        userService.deleteUserFromFriendsWhenUserDeleted(1);
//        userStorage.delete(1);
//        Optional<List<User>> userOptional = Optional.ofNullable(userStorage.getAll());
//
//        assertThat(userOptional)
//                .isPresent()
//                .hasValueSatisfying(user -> {
//                            assertThat(user.size() == 3).isTrue();
//                        }
//                );
//    }

	@Test
	@Order(3)
	public void testGetFilmById() {
		Optional<Film> filmOptional = Optional.ofNullable(filmDBStorage.getFilmById(1));

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1)
				);
	}

	@Test
	@Order(4)
	public void testGetAllFilms() {

		Optional<List<Film>> films = Optional.ofNullable(filmDBStorage.findAll());

		assertThat(films)
				.isPresent()
				.hasValueSatisfying(user -> {
							assertThat(user.size() == 3).isTrue();
						}
				);
	}

//    @Test
//    public void testDeleteFilmById() {
//        filmDBStorage.delete(1);
//        Optional<List<Film>> films = Optional.ofNullable(filmDBStorage.findAll());
//
//        assertThat(films)
//                .isPresent()
//                .hasValueSatisfying(film -> {
//                            assertThat(film.size() == 2).isTrue();
//                        }
//                );
//    }
}