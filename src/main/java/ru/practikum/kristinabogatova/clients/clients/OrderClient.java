package ru.practikum.kristinabogatova.clients.clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.practikum.kristinabogatova.clients.models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {
    private static final String ORDERS_PATH = "/api/v1/orders";

    @Step("Создать заказ")
    public ValidatableResponse create(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Получить список заказов")
    public ValidatableResponse getAll() {
        return given()
                .spec(getSpec())
                .when()
                .get(ORDERS_PATH)
                .then();
    }
}