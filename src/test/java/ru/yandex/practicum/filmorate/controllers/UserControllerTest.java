//package ru.yandex.practicum.filmorate.controllers;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Slf4j
//class UserControllerTest {
//    private static UserController userController;
//    private static User user1;
//    private static User user2;
//
//    private static User user3;
//    private static Validator validator;
//    private static Set<ConstraintViolation<User>> violations;
//
//    @BeforeAll
//    public static void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//        log.info("startup tests (users)");
//    }
//
//    @BeforeEach
//    void beforeEach() {
//        userController = new UserController(new UserService(new InMemoryUserStorage()));
//        user1 = new User("dolore", "Nick Name", "mail@mail.ru", LocalDate.parse("1946-08-20"));
//        user2 = new User("dolores", "", "mail@yandex.ru", LocalDate.parse("1956-08-20"));
//        user3 = new User("dolres", "gfgfh", "mail@bk.ru", LocalDate.parse("1966-08-20"));
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
//    void addUserTest() {
//        User resultUser = userController.create(user1);
//        user1.setId(1L);
//        assertEquals(user1.getId(), resultUser.getId());
//        assertEquals(user1.getName(), resultUser.getName());
//        assertEquals(user1.getLogin(), resultUser.getLogin());
//        assertEquals(user1.getEmail(), resultUser.getEmail());
//        assertEquals(user1.getBirthday(), resultUser.getBirthday());
//        log.info("Test: addUserTest");
//    }
//
//    @Test
//    void addDublicateUserTest() {
//        userController.create(user1);
//        final AlreadyExistException e = assertThrows(AlreadyExistException.class, () -> userController.create(user1));
//        assertEquals(e.getMessage(), "Данный пользователь уже добавлен");
//        log.info("Test: addDublicateUserTest");
//    }
//
//
//    /**
//     * Проверка name
//     */
//
//    @Test
//    public void ifNameIsNull_nameValidationTrue() {
//        User user = new User("dolore", null, "mail@mail.ru", LocalDate.parse("1946-08-20"));
//        violations = validator.validate(user);
//        assertTrue(violations.isEmpty());
//        log.info("Test: ifNameIsNull_nameValidationFails");
//    }
//
//    @Test
//    void addUserWithoutNameTest() {
//        User resultUser = userController.create(user2);
//        user2.setId(1L);
//        assertEquals(user2.getId(), resultUser.getId());
//        assertEquals(user2.getLogin(), resultUser.getName());
//        assertEquals(user2.getLogin(), resultUser.getLogin());
//        assertEquals(user2.getEmail(), resultUser.getEmail());
//        assertEquals(user2.getBirthday(), resultUser.getBirthday());
//        log.info("Test: addUserWithoutNameTest");
//    }
//
//    /**
//     * Проверка login
//     */
//
//    @Test
//    void negativeAddUserLoginWithWhitespace() {
//        User user = new User("dolore fghfjg", "Nick Name", "mail@mail.ru", LocalDate.parse("1946-08-20"));
//        violations = validator.validate(user);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "В логине не может содержаться пробел!");
//        }
//        log.info("Test: negativeAddUserLoginWithWhitespace");
//    }
//
//    @Test
//    void negativeAddUserBlankLogin() {
//        User user = new User("  ", "Nick Name", "email@mail.ru", LocalDate.parse("1946-08-20"));
//        violations = validator.validate(user);
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "В логине не может содержаться пробел!");
//        }
//        log.info("Test: negativeAddUserBlankLogin");
//    }
//
//    @Test
//    void ifEmailIsNull_loginValidationFails() {
//        User user = new User(null, "Nick Name", "email@mail.ru", LocalDate.parse("1946-08-20"));
//        violations = validator.validate(user);
//        assertEquals(violations.size(), 1);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "login cannot be null");
//        }
//        log.info("Test: ifEmailIsNull_loginValidationFails");
//    }
//
//
//    /**
//     * Проверка email
//     */
//
//    @Test
//    void negativeAddUserBlankEmail() {
//        User user = new User("dolore", "Nick Name", "  ", LocalDate.parse("1946-08-20"));
//        violations = validator.validate(user);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "Email is mandatory");
//        }
//        log.info("Test: negativeAddUserBlankEmail");
//    }
//
//    @Test
//    void ifEmailIsNull_emailValidationFails() {
//        User user = new User("dolore", "Nick Name", null, LocalDate.parse("1946-08-20"));
//        violations = validator.validate(user);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "Email cannot be null");
//        }
//        log.info("Test: ifEmailIsNull_nameValidationFails");
//    }
//
//    @Test
//    void negativeAddUserIncorrectEmail() {
//        User user = new User("dolore", "Nick Name", "mail", LocalDate.parse("1946-08-20"));
//        violations = validator.validate(user);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "Email is mandatory");
//        }
//        log.info("Test: negativeAddUserIncorrectEmail");
//    }
//
//    /**
//     * Проверка birthday
//     */
//
//    @Test
//    void negativeAddUserWithIncorrectFutureBirthday() {
//        User user = new User("gfjglfdg", "Nick Name", "email@mail.ru", LocalDate.parse("2946-08-20"));
//        violations = validator.validate(user);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "You cannot burn in future");
//        }
//        log.info("Test: negativeAddUserBlankLogin");
//    }
//
//    @Test
//    void ifBirthdayIsNull_loginValidationFails() {
//        User user = new User("fjkggj", "Nick Name", "email@mail.ru", null);
//        violations = validator.validate(user);
//        for (ConstraintViolation<User> violation : violations) {
//            assertEquals(violation.getMessage(), "Birthday cannot be null");
//        }
//        log.info("Test: ifEmailIsNull_loginValidationFails");
//    }
//
//    /**
//     * Проверка блока update
//     */
//
//    @Test
//    void updateUserTest(){
//        user1.setId(1L);
//        User resultUser = userController.update(user1);
//        assertEquals(user1.getId(), resultUser.getId());
//        assertEquals(user1.getName(), resultUser.getName());
//        assertEquals(user1.getLogin(), resultUser.getLogin());
//        assertEquals(user1.getEmail(), resultUser.getEmail());
//        assertEquals(user1.getBirthday(), resultUser.getBirthday());
//        log.info("Test: updateUserTest");
//    }
//
//    @Test
//    void duplicateUpdateUserTest(){
//        userController.create(user1);
//        user1.setId(1L);
//        User resultUser = userController.update(user1);
//        assertEquals(user1.getId(), resultUser.getId());
//        assertEquals(user1.getName(), resultUser.getName());
//        assertEquals(user1.getLogin(), resultUser.getLogin());
//        assertEquals(user1.getEmail(), resultUser.getEmail());
//        assertEquals(user1.getBirthday(), resultUser.getBirthday());
//        log.info("Test: duplicateUpdateUserTest");
//    }
//
//    /**
//     * Проверка блока put
//     */
//
//    @Test
//    void addNewFriendTest(){
//        userController.create(user1);
//        userController.create(user2);
//        userController.addNewFriendById(user1.getId(), user2.getId());
//        List<Long> listOfFriends = new ArrayList<>(user1.getFriends());
//        assertEquals(listOfFriends.get(0), 2);
//        log.info("Test: addNewFriendTest - отработан");
//    }
//
//    /**
//     * Проверка блока get
//     */
//
//    @Test
//    void getAllUsersTest(){
//        userController.create(user1);
//        userController.create(user2);
//        List<User> tempList = new ArrayList<>(userController.findAll());
//        assertEquals(tempList.size(), 2);
//        log.info("Test: getAllUsersTest - отработан");
//    }
//
//    @Test
//    void getAllUsersTestWithoutDuplicate(){
//        userController.create(user1);
//        user1.setId(1L);
//        userController.update(user1);
//        List<User> tempList = new ArrayList<>(userController.findAll());
//        assertEquals(tempList.size(), 1);
//        log.info("Test: getAllUsersTestWithoutDuplicate - отработан");
//    }
//
//    @Test
//    void returnListOfFriendsTest(){
//        userController.create(user1);
//        userController.create(user2);
//        userController.addNewFriendById(user1.getId(), user2.getId());
//        List<User> tempList = new ArrayList<>(userController.returnListOfFriends(user1.getId()));
//        assertEquals(tempList.size(), 1);
//        log.info("Test: returnListOfFriendsTest - отработан");
//    }
//
//    @Test
//    void getUserByIdTest(){
//        userController.create(user1);
//        userController.create(user2);
//        assertEquals(user2, userController.getUserById(2L));
//        log.info("Test: getUserByIdTest - отработан");
//    }
//
//    @Test
//    void returnListOfMutualFriendsTest(){
//        userController.create(user1);
//        userController.create(user2);
//        userController.create(user3);
//        userController.addNewFriendById(user1.getId(), user2.getId());
//        userController.addNewFriendById(user2.getId(), user3.getId());
//        List<User> tempList = new ArrayList<>(userController.getMutualFriendsList(user1.getId(), user3.getId()));
//        assertEquals(tempList.size(), 1);
//        log.info("Test: returnListOfMutualFriendsTest - отработан");
//    }
//
//    /**
//     * Проверка блока delete
//     */
//    @Test
//    void deleteFriendTest(){
//        userController.create(user1);
//        userController.create(user2);
//        userController.addNewFriendById(user1.getId(), user2.getId());
//        userController.deleteFriendById(user1.getId(), user2.getId());
//        List<User> tempList = new ArrayList<>(userController.returnListOfFriends(user1.getId()));
//        assertEquals(tempList.size(), 0);
//        log.info("Test: deleteFriendTest - отработан");
//    }
//}