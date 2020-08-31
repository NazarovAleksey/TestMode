package ru.netology.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.Value;
import java.util.Locale;

import static io.restassured.RestAssured.given;

@Data
public class DataGenerator {
   private DataGenerator() {
   }

    @Value
    public static class UserInfo {
        private String login;
        private String password;
        private String status;
    }

    public static UserInfo getUserInfo(boolean isBlocked) {
        Faker faker = new Faker(new Locale("en"));
        return new UserInfo(
                faker.name().username(),
                faker.internet().password(),
                (isBlocked) ? "blocked" : "active");
    }

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    static void setUpAll(UserInfo userInfo) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    public static UserInfo newUser(boolean isBlocked){
        UserInfo user = getUserInfo(isBlocked);
        setUpAll(user);
        return user;
    }
}