package ru.netology.test;

import com.codeborne.selenide.SelenideElement;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.UserData;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class AuthInvalidTest {
    SelenideElement form;

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @BeforeAll
    static void setUpAll() {
        Gson gson = new Gson();

        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(gson.toJson(new UserData("vasya","password","blocked"))) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        form = $(".form");
    }

    @Test
    void shouldNotLoginBlockedUser() {
        form.$("[data-test-id=login] input").setValue("vasya");
        form.$("[data-test-id=password] input").setValue("password");
        form.$("button.button").click();
        $("[data-test-id=error-notification]").waitUntil(visible, 5000);
        $("[data-test-id=error-notification]  .notification__title").shouldHave(exactText("Ошибка"));
        $("[data-test-id=error-notification]  .notification__content").shouldHave(exactText("Ошибка! Пользователь заблокирован"));
    }

    @Test
    void shouldNotLoginInvalidPassword() {
        form.$("[data-test-id=login] input").setValue("vasya");
        form.$("[data-test-id=password] input").setValue("pasword");
        form.$("button.button").click();
        $("[data-test-id=error-notification]").waitUntil(visible, 5000);
        $("[data-test-id=error-notification]  .notification__title").shouldHave(exactText("Ошибка"));
        $("[data-test-id=error-notification]  .notification__content").shouldHave(exactText("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldNotLoginInvalidLogin() {
        form.$("[data-test-id=login] input").setValue("test");
        form.$("[data-test-id=password] input").setValue("password");
        form.$("button.button").click();
        $("[data-test-id=error-notification]").waitUntil(visible, 5000);
        $("[data-test-id=error-notification]  .notification__title").shouldHave(exactText("Ошибка"));
        $("[data-test-id=error-notification]  .notification__content").shouldHave(exactText("Ошибка! Неверно указан логин или пароль"));
    }
}
