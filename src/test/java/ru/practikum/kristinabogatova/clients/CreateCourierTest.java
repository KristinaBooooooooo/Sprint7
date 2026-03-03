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

    @Test
    public void courierCanBeCreated() {
        Courier courier = TestDataGenerator.getRandomCourier();

        ValidatableResponse createResponse = courierClient.create(courier);
        createResponse.assertThat()
                .statusCode(SC_CREATED)
                .and()
                .body("ok", equalTo(true));

        ValidatableResponse loginResponse =
                courierClient.login(Credentials.from(courier));

        courierId = loginResponse.extract().path("id");
        assertNotNull(courierId);
    }

    @Test
    public void cannotCreateDuplicateCourier() {
        Courier courier = TestDataGenerator.getRandomCourier();

        ValidatableResponse firstResponse = courierClient.create(courier);
        ValidatableResponse secondResponse = courierClient.create(courier);

        firstResponse.assertThat().statusCode(SC_CREATED);
        secondResponse.assertThat()
                .statusCode(SC_CONFLICT)
                .and()
                .body("message", notNullValue());
        ValidatableResponse loginResponse = courierClient.login(Credentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertNotNull(courierId);
    }

    @Test
    public void createWithoutLoginFails() {
        Courier noLoginCourier = new Courier(null, "pass", "firstName");

        ValidatableResponse response = courierClient.create(noLoginCourier);

        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", notNullValue());
    }

    @Test
    public void createWithoutPasswordFails() {
        Courier noPasswordCourier = new Courier("login", null, "firstName");

        ValidatableResponse response = courierClient.create(noPasswordCourier);

        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", notNullValue());
    }
}