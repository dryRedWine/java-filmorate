package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.IllegalLoginException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserControllerTest {
    private static UserController userController;
    private static User user1;
    private static User user2;
    private static Validator validator;
    private static Set<ConstraintViolation<User>> violations;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        log.info("startup tests (users)");
    }

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        user1 = new User("dolore", "Nick Name", "mail@mail.ru", LocalDate.parse("1946-08-20"));
        user2 = new User("dolores", "", "mail@yandex.ru", LocalDate.parse("1956-08-20"));
    }

    @AfterAll
    public static void afterAll() {
        violations.clear();
        log.info("teardown");
    }

    /**
     * Проверка блока create
     */

    @Test
    void addUserTest() {
        User resultUser = userController.create(user1);
        user1.setId(1);
        assertEquals(user1.getId(), resultUser.getId());
        assertEquals(user1.getName(), resultUser.getName());
        assertEquals(user1.getLogin(), resultUser.getLogin());
        assertEquals(user1.getEmail(), resultUser.getEmail());
        assertEquals(user1.getBirthday(), resultUser.getBirthday());
        log.info("Test: addUserTest");
    }

    @Test
    void addDublicateUserTest() {
        userController.create(user1);
        final AlreadyExistException e = assertThrows(AlreadyExistException.class, () -> userController.create(user1));
        assertEquals(e.getMessage(), "Данный пользователь уже добавлен");
        log.info("Test: addDublicateUserTest");
    }


    /**
     * Проверка name
     */

    @Test
    public void ifNameIsNull_nameValidationTrue() {
        User user = new User("dolore", null, "mail@mail.ru", LocalDate.parse("1946-08-20"));
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
        log.info("Test: ifNameIsNull_nameValidationFails");
    }

    @Test
    void addUserWithoutNameTest() {
        User resultUser = userController.create(user2);
        user2.setId(1);
        assertEquals(user2.getId(), resultUser.getId());
        assertEquals(user2.getLogin(), resultUser.getName());
        assertEquals(user2.getLogin(), resultUser.getLogin());
        assertEquals(user2.getEmail(), resultUser.getEmail());
        assertEquals(user2.getBirthday(), resultUser.getBirthday());
        log.info("Test: addUserWithoutNameTest");
    }

    /**
     * Проверка login
     */

    @Test
    void negativeAddUserLoginWithWhitespace() {
        User user = new User("dolore fghfjg", "Nick Name", "mail@mail.ru", LocalDate.parse("1946-08-20"));
        final IllegalLoginException e = assertThrows(IllegalLoginException.class, () -> userController.create(user));
        assertEquals(e.getMessage(), "В логине не может содержаться пробел!");
        log.info("Test: negativeAddUserLoginWithWhitespace");
    }

    @Test
    void negativeAddUserBlankLogin() {
        User user = new User("  ", "Nick Name", "email@mail.ru", LocalDate.parse("1946-08-20"));
        violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals(violation.getMessage(), "login cannot be null or empty");
        }
        log.info("Test: negativeAddUserBlankLogin");
    }

    @Test
    void ifEmailIsNull_loginValidationFails() {
        User user = new User(null, "Nick Name", "email@mail.ru", LocalDate.parse("1946-08-20"));
        violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals(violation.getMessage(), "login cannot be null or empty");
        }
        log.info("Test: ifEmailIsNull_loginValidationFails");
    }


    /**
     * Проверка email
     */

    @Test
    void negativeAddUserBlankEmail() {
        User user = new User("dolore", "Nick Name", "  ", LocalDate.parse("1946-08-20"));
        violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals(violation.getMessage(), "Email is mandatory");
        }
        log.info("Test: negativeAddUserBlankEmail");
    }

    @Test
    void ifEmailIsNull_emailValidationFails() {
        User user = new User("dolore", "Nick Name", null, LocalDate.parse("1946-08-20"));
        violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals(violation.getMessage(), "Email cannot be null");
        }
        log.info("Test: ifEmailIsNull_nameValidationFails");
    }

    @Test
    void negativeAddUserIncorrectEmail() {
        User user = new User("dolore", "Nick Name", "mail", LocalDate.parse("1946-08-20"));
        violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals(violation.getMessage(), "Email is mandatory");
        }
        log.info("Test: negativeAddUserIncorrectEmail");
    }

    /**
     * Проверка birthday
     */

    @Test
    void negativeAddUserWithIncorrectFutureBirthday() {
        User user = new User("gfjglfdg", "Nick Name", "email@mail.ru", LocalDate.parse("2946-08-20"));
        violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals(violation.getMessage(), "You cannot burn in future");
        }
        log.info("Test: negativeAddUserBlankLogin");
    }

    @Test
    void ifBirthdayIsNull_loginValidationFails() {
        User user = new User("fjkggj", "Nick Name", "email@mail.ru", null);
        violations = validator.validate(user);
        for (ConstraintViolation<User> violation : violations) {
            assertEquals(violation.getMessage(), "Birthday cannot be null");
        }
        log.info("Test: ifEmailIsNull_loginValidationFails");
    }

    /**
     * Проверка блока update
     */

    @Test
    void updateUserTest(){
        user1.setId(1);
        User resultUser = userController.update(user1);
        assertEquals(user1.getId(), resultUser.getId());
        assertEquals(user1.getName(), resultUser.getName());
        assertEquals(user1.getLogin(), resultUser.getLogin());
        assertEquals(user1.getEmail(), resultUser.getEmail());
        assertEquals(user1.getBirthday(), resultUser.getBirthday());
        log.info("Test: updateUserTest");
    }

    @Test
    void duplicateUpdateUserTest(){
        userController.create(user1);
        user1.setId(1);
        User resultUser = userController.update(user1);
        assertEquals(user1.getId(), resultUser.getId());
        assertEquals(user1.getName(), resultUser.getName());
        assertEquals(user1.getLogin(), resultUser.getLogin());
        assertEquals(user1.getEmail(), resultUser.getEmail());
        assertEquals(user1.getBirthday(), resultUser.getBirthday());
        log.info("Test: duplicateUpdateUserTest");
    }

    /**
     * Проверка блока get
     */

    @Test
    void getAllUsersTest(){
        userController.create(user1);
        userController.create(user2);
        List<User> tempList = new ArrayList<>(userController.get());
        assertEquals(tempList.size(), 2);
    }

    @Test
    void getAllUsersTestWithoutDuplicate(){
        userController.create(user1);
        user1.setId(1);
        userController.update(user1);
        List<User> tempList = new ArrayList<>(userController.get());
        assertEquals(tempList.size(), 1);
    }
}