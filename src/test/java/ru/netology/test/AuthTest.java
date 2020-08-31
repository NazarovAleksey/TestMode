package ru.netology.test;

import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import java.util.Locale;

import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static ru.netology.data.DataGenerator.newUser;

public class AuthTest {

    Faker faker = new Faker(new Locale("en"));

    SelenideElement form;

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        form = $(".form");
    }

    void login(String login, String password) {
        $("[data-test-id=login] input").setValue(login);
        $("[data-test-id=password] input").setValue(password);
        $("[data-test-id=action-login]").click();
    }

    @Test
    public void shouldLoginValidUser() {
        DataGenerator.UserInfo user = newUser( false);
        login(user.getLogin(), user.getPassword());
        $("h2").shouldHave(exactText("Личный кабинет"));
    }


     @Test
    void shouldNotLoginInvalidPassword() {
         DataGenerator.UserInfo user = newUser( false);
         login(user.getLogin(), faker.internet().password());
         $(withText("Неверно указан логин или пароль")).waitUntil(visible, 5000);
    }

    @Test
    void shouldNotLoginInvalidLogin() {
        DataGenerator.UserInfo user = newUser( false);
        login(faker.name().username(), user.getPassword());
        $(withText("Неверно указан логин или пароль")).waitUntil(visible, 5000);
    }

    @Test
    public void shouldNotLoginBlockedUser() {
        DataGenerator.UserInfo user = newUser( true);
        login(user.getLogin(), user.getPassword());
        $(withText("Пользователь заблокирован")).waitUntil(visible, 5000);
    }
}
