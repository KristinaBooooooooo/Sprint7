package ru.practikum.kristinabogatova.clients.clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.practikum.kristinabogatova.clients.models.Courier;
import ru.practikum.kristinabogatova.clients.models.Credentials;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import java.net.SocketException;
import java.util.Map;
import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

public class CourierClient extends BaseClient {

    private static final String COURIER_PATH = "/api/v1/courier";
    private static final int MAX_RETRIES = 5;

    @Step("Создание курьера {courier.login}")
    public ValidatableResponse create(Courier courier) {
        return withRetry(() -> given()
                .spec(getSpec())
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then());
    }

    @Step("Логин курьера {credentials.login}")
    public ValidatableResponse login(Credentials credentials) {
        return withRetry(() -> given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(COURIER_PATH + "/login")
                .then());
    }

    @Step("Логин курьера с логином {login} (без пароля)")
    public ValidatableResponse loginWithLoginOnly(String login) {
        return withRetry(() -> given()
                .spec(getSpec())
                .body(Map.of("login", login))
                .when()
                .post(COURIER_PATH + "/login")
                .then());
    }

    @Step("Логин курьера с логином {login} и пустым паролем")
    public ValidatableResponse loginWithEmptyPassword(String login) {
        return withRetry(() -> given()
                .spec(getSpec())
                .body(Map.of("login", login, "password", ""))
                .when()
                .post(COURIER_PATH + "/login")
                .then());
    }

    @Step("Логин курьера с паролем {password} (без логина)")
    public ValidatableResponse loginWithPasswordOnly(String password) {
        return withRetry(() -> given()
                .spec(getSpec())
                .body(Map.of("password", password))
                .when()
                .post(COURIER_PATH + "/login")
                .then());
    }

    @Step("Удаление курьера с id {courierId}")
    public ValidatableResponse delete(int courierId) {
        return withRetry(() -> given()
                .spec(getSpec())
                .when()
                .delete(COURIER_PATH + "/" + courierId)
                .then());
    }

    private ValidatableResponse withRetry(Supplier<ValidatableResponse> action) {
        int attempts = 0;
        while (true) {
            try {
                return action.get();
            } catch (Exception e) {
                if (e instanceof SSLException || e instanceof SocketException) {
                    attempts++;
                    System.err.println("Network error (" + e.getClass().getSimpleName() + "), attempt " + attempts + " of " + MAX_RETRIES);
                    if (attempts >= MAX_RETRIES) {
                        throw new RuntimeException("Request failed after " + MAX_RETRIES + " attempts", e);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                } else {
                    throw e;
                }
            }
        }
    }
}