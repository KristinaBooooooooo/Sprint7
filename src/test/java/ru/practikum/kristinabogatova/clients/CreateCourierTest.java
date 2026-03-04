package ru.practikum.kristinabogatova.clients;

import ru.practikum.kristinabogatova.clients.clients.CourierClient;
import ru.practikum.kristinabogatova.clients.generators.TestDataGenerator;
import ru.practikum.kristinabogatova.clients.models.Courier;
import ru.practikum.kristinabogatova.clients.models.Credentials;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class CreateCourierTest {

    private final CourierClient courierClient = new CourierClient();
    private Integer courierId;

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.delete(courierId).assertThat().statusCode(SC_OK);
        }
    }

    private void loginAndSetId(Courier courier) {
        ValidatableResponse loginResponse = courierClient.login(Credentials.from(courier));
        loginResponse.assertThat().statusCode(SC_OK);
        courierId = loginResponse.extract().path("id");
        assertNotNull(courierId);
    }

    @Test
    public void courierCanBeCreated() {
        Courier courier = TestDataGenerator.getRandomCourier();

        ValidatableResponse createResponse = courierClient.create(courier);
        createResponse.assertThat()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        loginAndSetId(courier);
    }

    @Test
    public void cannotCreateDuplicateCourier() {
        Courier courier = TestDataGenerator.getRandomCourier();

        ValidatableResponse firstResponse = courierClient.create(courier);
        firstResponse.assertThat().statusCode(SC_CREATED);

        ValidatableResponse secondResponse = courierClient.create(courier);
        secondResponse.assertThat()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        loginAndSetId(courier);
    }

    @Test
    public void createWithoutLoginFails() {
        Courier noLoginCourier = new Courier(null, "pass", "firstName");

        ValidatableResponse response = courierClient.create(noLoginCourier);

        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void createWithoutPasswordFails() {
        Courier noPasswordCourier = new Courier("login", null, "firstName");

        ValidatableResponse response = courierClient.create(noPasswordCourier);

        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}