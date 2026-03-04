package ru.practikum.kristinabogatova.clients;

import ru.practikum.kristinabogatova.clients.clients.CourierClient;
import ru.practikum.kristinabogatova.clients.generators.TestDataGenerator;
import ru.practikum.kristinabogatova.clients.models.Courier;
import ru.practikum.kristinabogatova.clients.models.Credentials;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LoginCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = TestDataGenerator.getRandomCourier();
        courierClient.create(courier).assertThat().statusCode(SC_CREATED);
        ValidatableResponse loginResp = courierClient.login(Credentials.from(courier));
        courierId = loginResp.extract().path("id");
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.delete(courierId).assertThat().statusCode(SC_OK);
        }
    }

    @Test
    public void courierCanLogin() {
        ValidatableResponse response = courierClient.login(Credentials.from(courier));
        response.assertThat().statusCode(SC_OK);

        Integer id = response.extract().path("id");
        assertNotNull(id);
        assertEquals(courierId.intValue(), id.intValue());
    }

    @Test
    public void loginWithWrongPasswordFails() {
        ValidatableResponse response = courierClient.login(new Credentials(courier.getLogin(), "wrongpass"));

        response.assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void loginWithoutLoginFails() {
        ValidatableResponse response = courierClient.loginWithPasswordOnly(courier.getPassword());

        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void loginWithoutPasswordFails() {
        ValidatableResponse response = courierClient.loginWithEmptyPassword(courier.getLogin());

        response.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void loginNonExistentFails() {
        ValidatableResponse response = courierClient.login(new Credentials("no_such_user_" + System.currentTimeMillis(), "nopass"));

        response.assertThat()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}