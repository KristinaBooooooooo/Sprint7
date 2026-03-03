package ru.practikum.kristinabogatova.clients;

import ru.practikum.kristinabogatova.clients.clients.OrderClient;

import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class GetOrdersTest {

    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    public void ordersListReturned() {
        ValidatableResponse response = orderClient.getAll();
        response.assertThat().statusCode(SC_OK);

        List<Object> orders = response.extract().path("orders");

        assertNotNull(orders);
        if (!orders.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> orderMap = (Map<String, Object>) orders.get(0);
            assertTrue(orderMap.containsKey("id"));
        }
    }
}
