package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	private final FilmDbStorage filmDbStorage;
	private final UserDbStorage userDbStorage;
	private final UserService userServiceDb;
	private final FilmService filmServiceDb;


	@BeforeEach
	public void setUp() {

		User user = new User();
		user.setEmail("test@mail.ru");
		user.setLogin("testLogin");
		user.setName("Test User");
		user.setBirthday(new Date(85, 4, 20));
		userDbStorage.creat(user);

		User user2 = new User();
		user2.setEmail("example@yandex.ru");
		user2.setLogin("exampleLogin");
		user2.setName("Example User");
		user2.setBirthday(new Date(100, 7, 15));
		userDbStorage.creat(user2);

		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("This is a test film");
		film.setReleaseDate(new Date(121, 0, 1));
		film.setDuration(Duration.ofMinutes(120));
		Mpa mpa = new Mpa();
		mpa.setId(1);
		film.setMpa(mpa);
		filmDbStorage.create(film);
	}

	@Test
	public void testGetUserById() {
		Optional<User> user = userServiceDb.getUserById(1);
		assertThat(user).isPresent();
		assertThat(user.get())
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("email", "test@mail.ru")
				.hasFieldOrPropertyWithValue("login", "testLogin")
				.hasFieldOrPropertyWithValue("name", "Test User")
				.hasFieldOrPropertyWithValue("birthday", new Date(85, 4, 20));
	}

	@Test
	public void testGetFilmById() {
		Optional<Film> film = filmServiceDb.getFilmById(1);
		assertThat(film).isPresent();
		assertThat(film.get())
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "Test Film")
				.hasFieldOrPropertyWithValue("description", "This is a test film")
				.hasFieldOrPropertyWithValue("releaseDate", new Date(121, 0, 1))
				.hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(120));
	}

	@Test
	public void testAddFriends() {
		userServiceDb.addFriends(1, 2);
		List<User> friends = (List<User>) userServiceDb.getFriends(1); // Приведение к List<User> убрано
		assertEquals(1, friends.size());
		assertThat(friends.get(0))
				.hasFieldOrPropertyWithValue("id", 2);
	}

	@Test
	public void testLikeFilm() {
		filmServiceDb.setLike(1, 1);
		List<Film> popularFilms = (List<Film>) filmServiceDb.getPopular(1); // Приведение к List<Film> убрано
		assertThat(popularFilms).isNotEmpty();
		assertThat(popularFilms.get(0))
				.hasFieldOrPropertyWithValue("id", 1);
	}
}