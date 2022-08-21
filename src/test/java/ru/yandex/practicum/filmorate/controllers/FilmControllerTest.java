//package ru.yandex.practicum.filmorate.controllers;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.jdbc.core.JdbcTemplate;
//import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
//import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
//import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@Slf4j
//class FilmControllerTest {
//
//    private static FilmController filmController;
//    private static Film film1;
//    private static Film film2;
//    private static Validator validator;
//    private static Set<ConstraintViolation<Film>> violations;
//
//    @BeforeAll
//    public static void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//        log.info("startup tests (films)");
//    }
//
//    @BeforeEach
//    void beforeEach() {
//        filmController = new FilmController(new FilmService(new FilmDbStorage(new JdbcTemplate()),
//                new UserService(new UserDbStorage(new JdbcTemplate()))));
//        film1 = new Film("Avengers", "Something...", LocalDate.parse("1967-03-25"), 100);
//        film2 = new Film("Bad Santa 2", "Something...", LocalDate.parse("1967-03-25"), 120);
//    }
//
//    @AfterAll
//    public static void afterAll() {
//        violations.clear();
//        log.info("teardown");
//    }
//
//    /**
//     * Проверка блока create
//     */
//
//    @Test
//    void addFilmTest() {
//        Film resultFilm = filmController.create(film1);
//        film1.setId(1L);
//        assertEquals(film1.getId(), resultFilm.getId());
//        assertEquals(film1.getName(), resultFilm.getName());
//        assertEquals(film1.getDescription(), resultFilm.getDescription());
//        assertEquals(film1.getReleaseDate(), resultFilm.getReleaseDate());
//        assertEquals(film1.getDuration(), resultFilm.getDuration());
//        log.info("Test: addFilmTest");
//    }
//
//    @Test
//    void addDublicateFilmTest() {
//        filmController.create(film1);
//        final AlreadyExistException e = assertThrows(AlreadyExistException.class, () -> filmController.create(film1));
//        assertEquals(e.getMessage(), "Данный фильм уже добавлен");
//        log.info("Test: addDublicateFilmTest");
//    }
//
//
//    /**
//     * Проверка name
//     */
//
//    @Test
//    public void ifNameIsNull_FilmValidationFails() {
//        film1.setName(null);
//        violations = validator.validate(film1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "Name cannot be empty or null");
//        }
//        log.info("Test: ifNameIsNull_FilmValidationFails");
//    }
//    @Test
//    void negativeAddFilmBlankName() {
//        film1.setName("   ");
//        violations = validator.validate(film1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "Name cannot be empty or null");
//        }
//        log.info("Test: negativeAddFilmBlankName");
//    }
//
//    /**
//     * Проверка description
//     */
//
//    @Test
//    void negativeAddFilmBlankDescription() {
//        film1.setDescription("   ");
//        violations = validator.validate(film1);
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "Description cannot be empty or null");
//        }
//        log.info("Test: negativeAddFilmBlankDescription");
//    }
//
//    @Test
//    void ifDescriptionIsNull_FilmValidationFails() {
//        film1.setDescription(null);
//        violations = validator.validate(film1);
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "Description cannot be empty or null");
//        }
//        log.info("Test: ifDescriptionIsNull_FilmValidationFails");
//    }
//
//    @Test
//    void ifDescriptionSizeIs1_FilmValidationFails() {
//        film1 = new Film("Avengers", "h", LocalDate.parse("1967-03-25"), 100);
//        violations = validator.validate(film1);
//        // множество равно нулю, поэтому не попадает в цикл! при верном условии
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "Description must be between 1 and 200 characters");
//        }
//        log.info("Test: ifDescriptionOutOfSize_FilmValidationFails");
//    }
//
//    /**
//     * Проверка duration
//     */
//
//    @Test
//    void ifDurationIsNull_FilmValidationFails() {
//        film1.setDuration(null);
//        violations = validator.validate(film1);
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "Duration cannot be null");
//        }
//        log.info("Test: ifDurationIsNull_FilmValidationFails");
//    }
//
//    @Test
//    void ifDurationIsNotPositive_FilmValidationFails() {
//        film1.setDuration(-100);
//        violations = validator.validate(film1);
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "Duration can be only positive");
//        }
//        log.info("Test: ifDurationIsNotPositive_FilmValidationFails");
//    }
//
//
//    /**
//     * Проверка releaseDate
//     */
//
//    @Test
//    void ifReleaseDateIsNull_FilmValidationFails() {
//        film1.setReleaseDate(null);
//        violations = validator.validate(film1);
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<Film> violation : violations) {
//            assertEquals(violation.getMessage(), "ReleaseDate cannot be null");
//        }
//        log.info("Test: ifReleaseDateIsNull_FilmValidationFails");
//    }
//
//    @Test
//    void ifReleaseDateIsBefore_1895_FilmValidationFails() {
//        film1.setReleaseDate(LocalDate.parse("1800-01-10"));
//        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> filmController.create(film1));
//        assertEquals(e.getMessage(), "Выбрана ложная дата релиза");
//        log.info("Test: iifReleaseDateIsBefore_1895_FilmValidationFails");
//    }
//
//    /**
//     * Проверка блока update
//     */
//
//    @Test
//    void updateFilmTest() {
//        film1.setId(1L);
//        Film resultFilm = filmController.update(film1);
//        assertEquals(film1.getId(), resultFilm.getId());
//        assertEquals(film1.getName(), resultFilm.getName());
//        assertEquals(film1.getDescription(), resultFilm.getDescription());
//        assertEquals(film1.getReleaseDate(), resultFilm.getReleaseDate());
//        assertEquals(film1.getDuration(), resultFilm.getDuration());
//        log.info("Test: updateFilmTest");
//    }
//
//    @Test
//    void duplicateUpdateFilmTest() {
//        filmController.create(film1);
//        film1.setId(1L);
//        Film resultFilm = filmController.update(film1);
//        assertEquals(film1.getId(), resultFilm.getId());
//        assertEquals(film1.getName(), resultFilm.getName());
//        assertEquals(film1.getDescription(), resultFilm.getDescription());
//        assertEquals(film1.getReleaseDate(), resultFilm.getReleaseDate());
//        assertEquals(film1.getDuration(), resultFilm.getDuration());
//        log.info("Test: duplicateUpdateFilmTest");
//    }
//
//    /**
//     * Проверка блока put
//     */
//
//    @Test
//    void putLikeToFilm(){
//        filmController.create(film1);
//        filmController.putLikeToFilm(film1.getId(), 10L);
//        final  Set<Long> tempSet = new HashSet<>();
//        tempSet.add(10L);
//        assertEquals(film1.getLikes(), tempSet);
//        log.info("Test: putLikeToFilm - отработан");
//    }
//
//    /**
//     * Проверка блока get
//     */
//
//    @Test
//    void getAllFilmsTest() {
//        filmController.create(film1);
//        filmController.create(film2);
//        List<Film> tempList = new ArrayList<>(filmController.get());
//        assertEquals(tempList.size(), 2);
//    }
//
//    @Test
//    void getAllFilmsTestWithoutDuplicate() {
//        filmController.create(film1);
//        film1.setId(1L);
//        filmController.update(film1);
//        List<Film> tempList = new ArrayList<>(filmController.get());
//        System.out.println(tempList);
//        assertEquals(tempList.size(), 1);
//    }
//
//    @Test
//    void getByUserId(){
//        filmController.create(film1);
//        filmController.create(film2);
//        assertEquals(film2, filmController.getFilmById(2L));
//        log.info("Test: getByUserId - отработан");
//    }
//
//    @Test
//    void getPopularFilms2(){
//        filmController.create(film1);
//        filmController.create(film2);
//        filmController.putLikeToFilm(film1.getId(), 10L);
//        filmController.putLikeToFilm(film2.getId(), 10L);
//        filmController.putLikeToFilm(film2.getId(), 11L);
//        filmController.putLikeToFilm(film2.getId(), 21L);
//        List<Film> result = filmController.getPopularFilms(2L);
//        List<Film> toEqual = new ArrayList<>(List.of(film2, film1));
//        assertEquals(result, toEqual);
//        log.info("Test: getPopularFilms2 - отработан");
//    }
//
//    @Test
//    void getPopularFilms10ButLessInStorage(){
//        filmController.create(film1);
//        filmController.create(film2);
//        filmController.putLikeToFilm(film1.getId(), 10L);
//        filmController.putLikeToFilm(film2.getId(), 10L);
//        filmController.putLikeToFilm(film2.getId(), 11L);
//        filmController.putLikeToFilm(film2.getId(), 21L);
//        List<Film> result = filmController.getPopularFilms(10L);
//        List<Film> toEqual = new ArrayList<>(List.of(film2, film1));
//        assertEquals(result, toEqual);
//        log.info("Test: getPopularFilms10ButLessInStorage - отработан");
//    }
//
//    /**
//     * Проверка блока delete
//     */
//
//    @Test
//    void deleteLikeFromFilm(){
//        filmController.create(film1);
//        filmController.putLikeToFilm(film1.getId(), 10L);
//        filmController.deleteLikeToFilm(film1.getId(), 10L);
//        assertEquals(film1.getLikes().size(), 0);
//        log.info("Test: deleteLikeFromFilm - отработан");
//    }
//}