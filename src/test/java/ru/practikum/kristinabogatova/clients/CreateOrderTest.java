package ru.practikum.kristinabogatova.clients;

import ru.practikum.kristinabogatova.clients.clients.OrderClient;
import ru.practikum.kristinabogatova.clients.generators.TestDataGenerator;
import ru.practikum.kristinabogatova.clients.models.Order;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.List;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final Order order;
    private final OrderClient orderClient = new OrderClient();

    public CreateOrderTest(Order order) {
        this.order = order;
    }

    @Parameterized.Parameters(name = "order colors={0}")
    public static Object[][] getOrders() {
        return new Object[][]{
                {TestDataGenerator.getRandomOrder(List.of("BLACK"))},
                {TestDataGenerator.getRandomOrder(List.of("GREY"))},
                {TestDataGenerator.getRandomOrder(List.of("BLACK", "GREY"))},
                {TestDataGenerator.getRandomOrder(List.of())}, // без цвета
        };
    }

    @Test
    public void createOrderTest() {
        ValidatableResponse response = orderClient.create(order);
        response.statusCode(SC_CREATED);

        Integer track = response.extract().path("track");

        assertNotNull("track не должен быть null", track);
        assertTrue("Track должен быть больше 0", track > 0);
    }
}